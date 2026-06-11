package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.BossReward;
import gg.mmorealms.module.boss.backend.fabric.config.EffectConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.chat.common.dto.GlobalMessageEvent;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BossManager {

	private static final int MAX_REFILL_ANCHOR_ATTEMPTS = 5;
	private static final long BOOTSTRAP_DEBOUNCE_MS = 60_000L;

	private final BossConfig config;
	private final FileManager fileManager;

	private final Map<UUID, ActiveBoss> active = new ConcurrentHashMap<>();
	private final Map<BossTier, AtomicInteger> tierCounts = new EnumMap<>(BossTier.class);

	private final Map<UUID, CancelableTimeTask> despawnTasks = new ConcurrentHashMap<>();
	private final Map<UUID, CancelableTimeTask> particleTasks = new ConcurrentHashMap<>();

	private final Set<UUID> pendingDespawns = ConcurrentHashMap.newKeySet();
	/** Tiers with a short-retry already queued after a failed refill — prevents parallel retry chains. */
	private final Set<BossTier> refillRetryScheduled = ConcurrentHashMap.newKeySet();
	private @Nullable CancelableTimeTask pendingDespawnSweepTask;
	/** Entity UUIDs flagged to discard on next ENTITY_LOAD — persisted so admin despawns survive restarts. */
	private final BossPendingDiscards pendingDiscards;

	private final AtomicLong lastBootstrapAt = new AtomicLong(0);

	public BossManager(BossConfig config, FileManager fileManager) {
		this.config = config;
		this.fileManager = fileManager;
		this.pendingDiscards = fileManager.load(BossPendingDiscards.class);
		for (BossTier tier : BossTier.values()) {
			tierCounts.put(tier, new AtomicInteger(0));
		}
	}

	/* ---------- Tier reservation (atomic CAS) ---------- */

	public boolean tryReserveTier(@NotNull BossTier tier) {
		int cap = config.tiers.get(tier).maxActive;
		AtomicInteger ctr = tierCounts.get(tier);
		while (true) {
			int n = ctr.get();
			if (n >= cap) {
				return false;
			}
			if (ctr.compareAndSet(n, n + 1)) {
				return true;
			}
		}
	}

	public void forceReserveTier(@NotNull BossTier tier) {
		tierCounts.get(tier).incrementAndGet();
	}

	public void releaseTier(@NotNull BossTier tier) {
		tierCounts.get(tier).decrementAndGet();
	}

	/* ---------- Cache ---------- */

	public boolean registerActive(@NotNull ActiveBoss boss) {
		return active.putIfAbsent(boss.pokemonUUID(), boss) == null;
	}

	public @Nullable ActiveBoss get(@NotNull UUID pokemonUUID) {
		return active.get(pokemonUUID);
	}

	public boolean hasPendingDespawns() {
		return !pendingDespawns.isEmpty();
	}

	/** All active bosses. Iteration order not guaranteed. */
	public java.util.Collection<ActiveBoss> getAllActive() {
		return active.values();
	}

	/** Find boss by exact 8-char hex short-ID. Non-8-char inputs return null to avoid ambiguous matches. */
	public @Nullable ActiveBoss findByShortId(@NotNull String shortId) {
		if (shortId.length() != 8) return null;
		String lower = shortId.toLowerCase();
		for (ActiveBoss boss : active.values()) {
			if (boss.pokemonUUID().toString().toLowerCase().startsWith(lower)) {
				return boss;
			}
		}
		return null;
	}

	public static String shortId(@NotNull UUID pokemonUUID) {
		return pokemonUUID.toString().substring(0, 8);
	}

	/** Rollback helper for {@link BossSpawner} — undoes cache + tasks; caller releases tier counter. */
	public void rollbackRegistration(@NotNull UUID pokemonUUID) {
		active.remove(pokemonUUID);
		cancelTasks(pokemonUUID);
	}

	/* ---------- Scheduling (mirrors LegendaryDespawnManager pattern) ---------- */

	public void scheduleDespawn(@NotNull ActiveBoss boss) {
		TierConfig tc = config.tiers.get(boss.tier());
		long ms = tc.despawnAfter.toMilliseconds();
		if (ms <= 0) {
			Optional.ofNullable(despawnTasks.remove(boss.pokemonUUID())).ifPresent(CancelableTimeTask::cancel);
			return;
		}
		long elapsed = System.currentTimeMillis() - boss.spawnedAtEpoch();
		long remaining = ms - elapsed;
		if (remaining <= 0) {
			BossFabricModule.instance().runOnMain(() -> dispatchDespawn(boss));
			return;
		}
		CancelableTimeTask task = ScheduleUtils.runTaskLater(
				() -> BossFabricModule.instance().runOnMain(() -> dispatchDespawn(boss)),
				Time.milliseconds(remaining)
		);
		CancelableTimeTask prev = despawnTasks.put(boss.pokemonUUID(), task);
		if (prev != null) prev.cancel();
	}

	public void scheduleAmbientParticles(@NotNull ActiveBoss boss) {
		TierConfig tc = config.tiers.get(boss.tier());
		EffectConfig amb = tc.ambientEffect;
		if (amb == null || !amb.enabled || amb.intervalSeconds == null || amb.intervalSeconds <= 0) {
			Optional.ofNullable(particleTasks.remove(boss.pokemonUUID())).ifPresent(CancelableTimeTask::cancel);
			return;
		}
		CancelableTimeTask task = ScheduleUtils.runTaskTimer(
				() -> emitAmbient(boss, amb),
				Time.seconds(amb.intervalSeconds)
		);
		CancelableTimeTask prev = particleTasks.put(boss.pokemonUUID(), task);
		if (prev != null) prev.cancel();
	}

	private void emitAmbient(@NotNull ActiveBoss boss, @NotNull EffectConfig amb) {
		BossFabricModule.instance().runOnMain(() -> {
			PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
			if (entity == null || entity.isRemoved()) {
				// Unloaded ≠ dead — stop particles only; the despawn timer keeps running.
				// Particles reschedule via handleEntityLoad on chunk reload.
				cancelParticleTask(boss.pokemonUUID());
				return;
			}
			ServerLevel sl = (ServerLevel) entity.level();
			double cy = entity.getY() + entity.getBbHeight() / 2.0;
			for (var p : amb.particleOptions) {
				sl.sendParticles(p, entity.getX(), cy, entity.getZ(),
						amb.count, amb.offset, amb.offset, amb.offset, 0.0);
			}
		});
	}

	private void cancelParticleTask(@NotNull UUID pokemonUUID) {
		Optional.ofNullable(particleTasks.remove(pokemonUUID)).ifPresent(CancelableTimeTask::cancel);
	}

	private void cancelTasks(@NotNull UUID pokemonUUID) {
		Optional.ofNullable(despawnTasks.remove(pokemonUUID)).ifPresent(CancelableTimeTask::cancel);
		cancelParticleTask(pokemonUUID);
		pendingDespawns.remove(pokemonUUID);
	}

	/* ---------- Consolidated cleanup ----------
	 * One method owns the recipe so the 4 paths can't drift.
	 */

	public enum CleanupKind {
		/** Boss defeated by a player — rewards + announcement + discard entity. */
		DEFEAT,
		/** Boss defeated but no rewardable target (winner offline / AI-only winner) — discard entity, no rewards; GLOBAL_CHAT defeat announcement still fires. */
		DEFEAT_NO_REWARD,
		/** Despawn timer / admin despawn — discard entity, no rewards/announcement. */
		AUTO_DESPAWN
	}

	public void handleDefeat(@NotNull UUID pokemonUUID, @NotNull ServerPlayer winner) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT, winner, null, null);
	}

	public void handleDefeatWithoutReward(@NotNull UUID pokemonUUID, @Nullable UUID winnerUUID, @NotNull String reason) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT_NO_REWARD, null, winnerUUID, reason);
	}

	/** Returns true (and clears the persisted flag) if this entityUUID was flagged for hard-despawn-on-load. */
	public boolean consumePendingDiscard(@NotNull UUID entityUUID) {
		if (!pendingDiscards.entityUUIDs.remove(entityUUID)) {
			return false;
		}
		savePendingDiscards();
		return true;
	}

	/** writeFile, not save() — save()'s writeFileAndBackup would litter a timestamped backup on every change. */
	private void savePendingDiscards() {
		fileManager.writeFile("", "boss_pending_discards.json", BossFabricModule.instance().toJson(pendingDiscards));
	}

	/** Fill every tier up to {@link TierConfig#minActive}, staggered 5s apart. Debounced — JOIN fires this on every login. */
	public void bootstrapFillAllTiers() {
		long now = System.currentTimeMillis();
		long previous = lastBootstrapAt.get();
		if (now - previous < BOOTSTRAP_DEBOUNCE_MS || !lastBootstrapAt.compareAndSet(previous, now)) {
			return;
		}
		Logger.info("Bootstrap: scheduling staggered refill checks for all tiers");
		BossTier[] tiers = BossTier.values();
		// All tiers (incl. index 0) get a +5s buffer so the player-join race can't skip the first tier.
		for (int i = 0; i < tiers.length; i++) {
			BossTier tier = tiers[i];
			ScheduleUtils.runTaskLater(() -> tryRefillTier(tier), Time.seconds((i + 1) * 5L));
		}
	}

	/** Low-frequency safety net — re-attempts refills that failed (e.g. POSITION_NOT_FOUND) without waiting for a join or boss death. */
	public void refillSweep() {
		for (BossTier tier : BossTier.values()) {
			tryRefillTier(tier);
		}
	}

	/** Ordered for crash-safety: remove → release → cancel tasks → clear NBT → rewards → discard → announce. */
	private void cleanup(@NotNull UUID pokemonUUID, @NotNull CleanupKind kind,
	                     @Nullable ServerPlayer winner, @Nullable UUID offlineWinnerUUID, @Nullable String reason) {
		ActiveBoss boss = active.remove(pokemonUUID);
		if (boss == null) {
			Logger.debug("Boss cleanup [" + kind + "] skipped — UUID " + pokemonUUID
					+ " already removed by another path.");
			return; // already processed by another path — atomic guard
		}
		Logger.info("Boss cleanup [" + kind + "] " + boss.tier() + " " + boss.species()
				+ " lv." + boss.level() + " (" + shortId(pokemonUUID) + ")"
				+ (boss.systemSpawned() ? " [system]" : " [admin]")
				+ (winner != null ? " winner=" + winner.getGameProfile().getName() : ""));
		// Only system bosses live in the per-tier counter; admin spawns never touch it.
		if (boss.systemSpawned()) {
			releaseTier(boss.tier());
		}
		cancelTasks(pokemonUUID);

		MinecraftServer server = BossFabricModule.instance().getServer();
		PokemonEntity entity = null;
		if (server != null) {
			removeBossTeam(server, pokemonUUID);
			entity = BossFabricModule.instance().findEntity(boss.entityUUID());
		}

		// Clear NBT marker BEFORE rewards/discard so a crash between here and discard leaves a
		// plain Pokemon, not a re-fightable boss with intact rewards.
		if (entity != null) {
			entity.getPokemon().getPersistentData().putBoolean(BossNbtKeys.BOSS, false);
		} else {
			pendingDiscards.entityUUIDs.add(boss.entityUUID());
			savePendingDiscards();
			Logger.info("Boss entity " + boss.entityUUID() + " not loaded at cleanup [" + kind
					+ "]; flagged for hard-despawn on next chunk load.");
		}

		if (kind == CleanupKind.DEFEAT && winner != null) {
			TierConfig tc = config.tiers.get(boss.tier());
			sendPersonalDefeat(winner, boss, tc);
			dispatchRewards(boss, winner);
		}

		if (entity != null) {
			entity.discard();
		}

		if (kind == CleanupKind.DEFEAT && winner != null) {
			announceDefeat(boss, usernameOf(winner), winner.serverLevel());
		} else if (kind == CleanupKind.DEFEAT_NO_REWARD) {
			IUser offlineUser = offlineWinnerUUID != null ? IUser.getByUUID(offlineWinnerUUID) : null;
			announceDefeat(boss, offlineUser != null ? offlineUser.getUsername() : "Unknown Trainer", null);
		}

		if (reason != null) {
			Logger.info("Boss " + boss.tier() + " " + boss.species() + " (" + pokemonUUID
					+ ") cleanup [" + kind + "]: " + reason);
		}

		// One-shot refill if a system boss died — admin removals don't affect the floor.
		if (boss.systemSpawned()) {
			tryRefillTier(boss.tier());
		}
	}

	private void sendPersonalDefeat(@NotNull ServerPlayer winner, @NotNull ActiveBoss boss, @NotNull TierConfig tc) {
		sendToWinner(winner, config.lang.bossPersonalDefeat
				.parse("glow_color", tc.glowColor.toLowerCase())
				.parse("tier_display", tc.displayName)
				.parse("species", boss.species())
				.parse());
	}

	/** Refill a tier up to {@link TierConfig#minActive}. */
	private void tryRefillTier(@NotNull BossTier tier) {
		TierConfig tc = config.tiers.get(tier);
		if (tc == null || tc.minActive <= 0) {
			return;
		}
		if (tierCounts.get(tier).get() >= tc.minActive) {
			return;
		}
		BossFabricModule mod = BossFabricModule.instance();
		MinecraftServer s = mod != null ? mod.getServer() : null;
		if (s == null) return;
		s.execute(() -> {
			List<String> allowedDims = SpawnPositionFinder.allowedDimensions(tc, config);
			List<ServerPlayer> anchors = new ArrayList<>();
			for (ServerPlayer player : s.getPlayerList().getPlayers()) {
				if (allowedDims.contains(player.serverLevel().dimension().location().toString())) {
					anchors.add(player);
				}
			}
			if (anchors.isEmpty()) {
				Logger.debug("Refill skipped for tier " + tier + ": no players in an allowed dimension; retrying in 30s.");
				scheduleRefillRetry(tier);
				return;
			}
			BossSpawner spawner = mod.getBossSpawner();
			if (spawner == null) return;
			Collections.shuffle(anchors);
			List<ServerPlayer> candidates = anchors.size() > MAX_REFILL_ANCHOR_ATTEMPTS
					? anchors.subList(0, MAX_REFILL_ANCHOR_ATTEMPTS)
					: anchors;
			while (tierCounts.get(tier).get() < tc.minActive) {
				BossSpawner.AdminSpawnResult result = null;
				for (ServerPlayer anchor : candidates) {
					result = spawner.systemRefillSpawn(anchor, tier);
					if (result.status() == BossSpawner.AdminSpawnStatus.SUCCESS) {
						Logger.info("Boss tier " + tier + " refilled near " + anchor.getGameProfile().getName());
						break;
					}
					Logger.debug("Refill anchor " + anchor.getGameProfile().getName() + " failed for tier "
							+ tier + ": " + result.status());
				}
				if (result == null || result.status() != BossSpawner.AdminSpawnStatus.SUCCESS) {
					Logger.warn("Refill spawn for tier " + tier + " failed near " + candidates.size()
							+ " anchor(s)" + (result != null ? " (last: " + result.status() + ")" : "")
							+ "; retrying in 30s.");
					scheduleRefillRetry(tier);
					return;
				}
			}
		});
	}

	/** Short retry after a failed refill. */
	private void scheduleRefillRetry(@NotNull BossTier tier) {
		if (!refillRetryScheduled.add(tier)) {
			return;
		}
		ScheduleUtils.runTaskLater(() -> {
			refillRetryScheduled.remove(tier);
			tryRefillTier(tier);
		}, Time.seconds(30));
	}

	public void retryPendingDespawns() {
		// Snapshot before iterating so removals can't race the iterator.
		List<UUID> snapshot = new ArrayList<>(pendingDespawns);
		for (UUID uuid : snapshot) {
			ActiveBoss boss = active.get(uuid);
			if (boss == null) {
				pendingDespawns.remove(uuid);
				continue;
			}
			PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
			if (entity == null || entity.isRemoved() || !entity.isBusy()) {
				pendingDespawns.remove(uuid);
				cleanup(uuid, CleanupKind.AUTO_DESPAWN, null, null, null);
			}
		}
	}

	/* ---------- Admin despawn router ---------- */

	/** Outcome of a single boss despawn request — used by callers to pick the admin reply. */
	public enum DespawnOutcome { CLEANED, QUEUED_BATTLE }

	/** Per-boss dispatch — queues if mid-battle (would corrupt battle state), else cleans up. */
	public DespawnOutcome dispatchDespawn(@NotNull ActiveBoss boss) {
		PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
		Logger.info("dispatchDespawn " + shortId(boss.pokemonUUID()) + " — entity=" + (entity != null)
				+ " removed=" + (entity != null && entity.isRemoved())
				+ " busy=" + (entity != null && entity.isBusy()));
		if (entity != null && entity.isBusy()) {
			pendingDespawns.add(boss.pokemonUUID());
			startPendingDespawnSweep();
			return DespawnOutcome.QUEUED_BATTLE;
		}
		cleanup(boss.pokemonUUID(), CleanupKind.AUTO_DESPAWN, null, null, "admin despawn");
		return DespawnOutcome.CLEANED;
	}

	/** Timer fallback for queued mid-battle despawns — battle-end events can be missed (e.g. mid-battle disconnect). */
	private synchronized void startPendingDespawnSweep() {
		if (pendingDespawnSweepTask != null && !pendingDespawnSweepTask.isCanceled()) {
			return;
		}
		pendingDespawnSweepTask = ScheduleUtils.runTaskTimer(this::sweepPendingDespawns, Time.seconds(15));
	}

	private void sweepPendingDespawns() {
		if (pendingDespawns.isEmpty()) {
			synchronized (this) {
				if (pendingDespawnSweepTask != null) {
					pendingDespawnSweepTask.cancel();
					pendingDespawnSweepTask = null;
				}
			}
			return;
		}
		BossFabricModule.instance().runOnMain(this::retryPendingDespawns);
	}

	/* ---------- Rewards ---------- */

	private void dispatchRewards(@NotNull ActiveBoss boss, @NotNull ServerPlayer winner) {
		TierConfig tc = config.tiers.get(boss.tier());
		if (tc.rewardRolls <= 0 || tc.rewards.isEmpty()) {
			Logger.info("Boss " + boss.tier() + " " + boss.species()
					+ ": no rewards dispatched (rewardRolls=" + tc.rewardRolls
					+ ", rewards.size=" + tc.rewards.size() + ")");
			return;
		}
		String username = usernameOf(winner);
		Logger.info("Dispatching " + tc.rewardRolls + " reward roll(s) for boss " + boss.tier()
				+ " " + boss.species() + " to " + username);

		String glow = tc.glowColor.toLowerCase();
		sendToWinner(winner, config.lang.bossRewardWinnerHeader
				.parse("glow_color", glow)
				.parse("tier_display", tc.displayName)
				.parse("species", boss.species())
				.parse());

		List<String> rolledEntries = new ArrayList<>(tc.rewardRolls);
		for (int i = 0; i < tc.rewardRolls; i++) {
			BossReward reward = RandomUtils.getRandomWeighed(tc.rewards);
			// Floor at 1 — a Range with min=0 would otherwise produce silent zero-qty `give` commands.
			int quantity = Math.max(1, (int) RandomUtils.getRandom(reward.getQuantity()));
			// MessageBuilderList.parse(k,v) clones internally — chain is safe without explicit clone.
			List<String> commands = reward.getRewardCommands()
					.parse("user", username)
					.parse("uuid", winner.getUUID().toString())
					.parse("tier", boss.tier().name())
					.parse("tier_display", tc.displayName)
					.parse("species", boss.species())
					.parse("level", boss.level())
					.parse("boss_uuid", boss.pokemonUUID().toString())
					.parse("entity_uuid", boss.entityUUID().toString())
					.parse("quantity", quantity)
					.parse();
				boolean commandsSucceeded = true;
				for (String cmd : commands) {
					try {
						if (!executeRewardCommand(cmd)) {
							commandsSucceeded = false;
							Logger.warn("Reward command returned no success for boss " + boss.pokemonUUID()
									+ " (tier=" + boss.tier() + ", species=" + boss.species()
									+ ", winner=" + winner.getUUID() + "): " + cmd);
						}
					} catch (Throwable t) {
						commandsSucceeded = false;
						Logger.warn("Reward command failed for boss " + boss.pokemonUUID()
								+ " (tier=" + boss.tier() + ", species=" + boss.species()
							+ ", winner=" + winner.getUUID() + "): " + cmd
							+ " — " + t.getClass().getSimpleName() + ": " + t.getMessage());
				}
			}
			// Only list rewards whose commands all succeeded — the summary must not overstate.
			if (commandsSucceeded) {
				String display = reward.getDisplayName() != null && !reward.getDisplayName().isBlank()
						? reward.getDisplayName()
						: extractDisplayLabel(commands);
				rolledEntries.add(quantity + "x " + display);
			}
		}

		if (!rolledEntries.isEmpty()) {
			sendToWinner(winner, config.lang.bossRewardWinnerSummary
					.parse("glow_color", glow)
					.parse("rewards", String.join(", ", rolledEntries))
					.parse());
			}
		}

	private boolean executeRewardCommand(@NotNull String cmd) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		MinecraftServer server = BossFabricModule.instance().getServer();
		if (server == null) {
			return false;
		}
		return server.getCommands().getDispatcher().execute(cmd, server.createCommandSourceStack()) > 0;
	}

	/** Prefer IUser for nickname-aware username (matches gyms convention). */
	private static String usernameOf(@NotNull ServerPlayer player) {
		IUser user = IUser.getByUUID(player.getUUID());
		return user != null ? user.getUsername() : player.getGameProfile().getName();
	}

	private void sendToWinner(@NotNull ServerPlayer winner, @NotNull String parsedMiniMessage) {
		winner.sendSystemMessage(BossFabricModule.instance().getMiniMessageManager().parse(parsedMiniMessage));
	}

	/** Best-effort label from a reward command — looks for {@code namespace:slug}. Set {@code displayName} explicitly for other shapes. */
	private static String extractDisplayLabel(@NotNull List<String> commands) {
		if (commands.isEmpty()) return "reward";
		for (String token : commands.get(0).split("\\s+")) {
			if (token.startsWith("{") || !token.contains(":")) continue;
			String slug = token.substring(token.indexOf(':') + 1);
			return titleCase(slug.replace('_', ' '));
		}
		return "reward";
	}

	private static final Set<String> ACRONYM_TOKENS = Set.of("xl", "xs", "ev", "iv", "hp", "pp");

	/** Title-case words; uppercase known acronyms. "exp candy xl" → "Exp Candy XL". */
	private static String titleCase(@NotNull String input) {
		String[] words = input.split(" ");
		StringBuilder out = new StringBuilder(input.length());
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.isEmpty()) continue;
			if (i > 0) out.append(' ');
			if (ACRONYM_TOKENS.contains(word.toLowerCase())) {
				out.append(word.toUpperCase());
			} else {
				out.append(Character.toUpperCase(word.charAt(0)));
				if (word.length() > 1) out.append(word.substring(1).toLowerCase());
			}
		}
		return out.toString();
	}

	/* ---------- Scoreboard team (single source of truth across spawn + reload) ---------- */

	private static String teamName(@NotNull UUID pokemonUUID) {
		return "boss_" + pokemonUUID.toString().substring(0, 8);
	}

	public void applyBossTeam(@NotNull MinecraftServer server, @NotNull PokemonEntity entity, @NotNull TierConfig tc) {
		ServerScoreboard sb = server.getScoreboard();
		String name = teamName(entity.getPokemon().getUuid());
		PlayerTeam team = sb.getPlayerTeam(name);
		boolean freshTeam = (team == null);
		if (freshTeam) {
			team = sb.addPlayerTeam(name);
		}
		// Skip no-op writes — both setColor and addPlayerToTeam broadcast to all players.
		ChatFormatting color = tc.glowChatFmt != null ? tc.glowChatFmt : ChatFormatting.WHITE;
		boolean colorFixed = team.getColor() != color;
		if (colorFixed) {
			team.setColor(color);
		}
		String scoreboardName = entity.getScoreboardName();
		boolean memberFixed = !team.getPlayers().contains(scoreboardName);
		if (memberFixed) {
			sb.addPlayerToTeam(scoreboardName, team);
		}
		boolean glowFixed = !entity.hasGlowingTag();
		entity.setGlowingTag(true);
		// Drift on a previously-applied boss means something cleared glow state — log it so the
		// loss vector shows up in server logs instead of only as a missing outline in-game.
		if (!freshTeam && (colorFixed || memberFixed || glowFixed)) {
			Logger.info("Boss glow drift repaired on " + scoreboardName + " (team=" + name + "):"
					+ (colorFixed ? " color" : "") + (memberFixed ? " membership" : "") + (glowFixed ? " glowFlag" : ""));
		}
	}

	/** Periodic re-assert of team color + membership + glow flag for every loaded boss. Idempotent — no packets when state is already correct. */
	public void glowSweep() {
		if (active.isEmpty()) {
			return;
		}
		BossFabricModule mod = BossFabricModule.instance();
		MinecraftServer server = mod != null ? mod.getServer() : null;
		if (server == null) {
			return;
		}
		server.execute(() -> {
			for (ActiveBoss boss : active.values()) {
				PokemonEntity entity = mod.findEntity(boss.entityUUID());
				if (entity == null || entity.isRemoved()) {
					continue;
				}
				TierConfig tc = config.tiers.get(boss.tier());
				if (tc != null) {
					applyBossTeam(server, entity, tc);
				}
			}
		});
	}

	public void removeBossTeam(@NotNull MinecraftServer server, @NotNull UUID pokemonUUID) {
		ServerScoreboard sb = server.getScoreboard();
		PlayerTeam team = sb.getPlayerTeam(teamName(pokemonUUID));
		if (team != null) {
			sb.removePlayerTeam(team);
		}
	}

	/* ---------- Announce helper ---------- */

	/** {@code worldLevel} is null when the winner is offline — WORLD_CHAT has no target then and stays silent; GLOBAL_CHAT still fires. */
	private void announceDefeat(@NotNull ActiveBoss boss, @NotNull String username, @Nullable ServerLevel worldLevel) {
		TierConfig tc = config.tiers.get(boss.tier());
		gg.mmorealms.module.boss.common.AnnounceLevel lvl = tc.announceOnDefeat;
		if (lvl == null || lvl == gg.mmorealms.module.boss.common.AnnounceLevel.OFF) {
			return;
		}
		String glow = tc.glowColor.toLowerCase();

		switch (lvl) {
			case WORLD_CHAT -> {
				if (worldLevel == null) {
					return;
				}
				String message = config.lang.bossDefeatedAnnouncementWorld
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", boss.species())
						.parse("player", username)
						.parse();
				net.minecraft.network.chat.Component comp = BossFabricModule.instance()
						.getMiniMessageManager().parse(message);
				for (ServerPlayer p : worldLevel.players()) {
					p.sendSystemMessage(comp);
				}
			}
			case GLOBAL_CHAT -> {
				String message = config.lang.bossDefeatedAnnouncementGlobal
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", boss.species())
						.parse("player", username)
						.parse();
				new GlobalMessageEvent(message).send();
			}
			default -> {}
		}
	}
}
