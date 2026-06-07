package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BossManager {

	private final BossConfig config;

	private final Map<UUID, ActiveBoss> active = new ConcurrentHashMap<>();
	private final Map<BossTier, AtomicInteger> tierCounts = new EnumMap<>(BossTier.class);

	private final Map<UUID, CancelableTimeTask> despawnTasks = new ConcurrentHashMap<>();
	private final Map<UUID, CancelableTimeTask> particleTasks = new ConcurrentHashMap<>();

	private final Set<UUID> pendingDespawns = ConcurrentHashMap.newKeySet();
	/**
	 * Boss entity UUIDs whose entity wasn't loaded at cleanup time. On the next
	 * {@code ENTITY_LOAD} the entity is discarded (and NBT marker cleared) instead of
	 * being re-registered — otherwise admin despawns are silently reverted on chunk
	 * reload (player walks away + comes back → boss is back).
	 */
	private final Set<UUID> pendingDiscardOnLoad = ConcurrentHashMap.newKeySet();

	public BossManager(BossConfig config) {
		this.config = config;
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

	/**
	 * Find an active boss whose pokemonUUID starts with this exactly-8-character hex short ID.
	 * Returns null for any input that is not exactly 8 chars long — partial prefixes are
	 * rejected to prevent accidentally targeting the wrong boss.
	 */
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

	/**
	 * Internal-use rollback for {@link BossSpawner}: undo cache + tasks for a partially-spawned boss
	 * without releasing the tier counter (caller releases once, after all rollback steps).
	 */
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
				cancelTasks(boss.pokemonUUID());
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

	private void cancelTasks(@NotNull UUID pokemonUUID) {
		Optional.ofNullable(despawnTasks.remove(pokemonUUID)).ifPresent(CancelableTimeTask::cancel);
		Optional.ofNullable(particleTasks.remove(pokemonUUID)).ifPresent(CancelableTimeTask::cancel);
		pendingDespawns.remove(pokemonUUID);
	}

	/* ---------- Consolidated cleanup ----------
	 * One method owns the recipe so the 4 paths can't drift.
	 */

	public enum CleanupKind {
		/** Boss defeated by a player — rewards + announcement + discard entity. */
		DEFEAT,
		/** Boss defeated but no rewardable target (winner offline / AI-only winner) — discard entity, no rewards/announcement. */
		DEFEAT_NO_REWARD,
		/** Despawn timer / admin despawn — discard entity, no rewards/announcement. */
		AUTO_DESPAWN
	}

	public void handleDefeat(@NotNull UUID pokemonUUID, @NotNull ServerPlayer winner) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT, winner, null);
	}

	public void handleDefeatWithoutReward(@NotNull UUID pokemonUUID, @NotNull String reason) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT_NO_REWARD, null, reason);
	}

	/**
	 * Was the given entityUUID flagged for hard-despawn-on-load? Removes the flag if so.
	 * Called from {@code handleEntityLoad}.
	 */
	public boolean consumePendingDiscard(@NotNull UUID entityUUID) {
		return pendingDiscardOnLoad.remove(entityUUID);
	}

	/**
	 * Fill every tier up to {@link TierConfig#minActive}, staggered 5 seconds apart so the
	 * first player to join after a server restart sees announcements drip in instead of a
	 * 7-line flurry on a single tick. Idempotent — each tier short-circuits if already at floor.
	 * tryRefillTier internally hops to main for the actual spawn, so off-main scheduling is safe.
	 */
	public void bootstrapFillAllTiers() {
		Logger.info("bootstrapFillAllTiers called — scheduling staggered refill checks for all 7 tiers");
		BossTier[] tiers = BossTier.values();
		for (int i = 0; i < tiers.length; i++) {
			BossTier tier = tiers[i];
			if (i == 0) {
				tryRefillTier(tier);
			} else {
				ScheduleUtils.runTaskLater(() -> tryRefillTier(tier), Time.seconds(i * 5L));
			}
		}
	}

	/**
	 * Ordering matters for crash-safety:
	 *   1. Atomic active.remove → cache+tier guard.
	 *   2. releaseTier.
	 *   3. cancelTasks.
	 *   4. removeBossTeam.
	 *   5. Clear {@code mmo_realms_boss} NBT marker BEFORE reward dispatch — if the JVM dies between
	 *      marker-clear and any later step, the entity reloads as a non-boss Pokemon (no re-fight,
	 *      no double rewards). If the entity is missing at this point, log WARN — duplicate
	 *      prevention cannot be guaranteed if the chunk later reloads with a stale marker.
	 *   6. dispatchRewards (DEFEAT only).
	 *   7. discard entity if loaded; else flag entityUUID for hard-despawn-on-reload so a
	 *      chunk reload can't re-register the boss.
	 *   8. announceDefeat (DEFEAT only).
	 */
	private void cleanup(@NotNull UUID pokemonUUID, @NotNull CleanupKind kind,
	                     @Nullable ServerPlayer winner, @Nullable String reason) {
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
			pendingDiscardOnLoad.add(boss.entityUUID());
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
			announceDefeat(boss, winner);
		}

		if (reason != null) {
			Logger.info("Boss " + boss.tier() + " " + boss.species() + " (" + pokemonUUID
					+ ") cleanup [" + kind + "]: " + reason);
		}

		// Immediate one-shot refill if the tier dropped below its floor. Only meaningful for
		// system bosses — admin spawns don't reduce the floor when removed.
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

	/**
	 * Backend-local refill — when system-spawned active count drops below
	 * {@link TierConfig#minActive}, spawn until the floor is met (or the first spawn fails).
	 * Skips when the floor is 0 (refill disabled) or no online players exist.
	 * <p>
	 * Recheck + player lookup + spawn loop all run inside the main-thread closure.
	 * Main-thread execution serializes queued refill tasks, so concurrent bootstrap/JOIN
	 * triggers can't double-spawn — the second closure sees the floor already met and bails.
	 * <p>
	 * The loop bails on the first failed spawn (e.g. SpawnPositionFinder can't find a spot)
	 * to avoid infinite tight-loop retries on persistent failure. Iteration cap = minActive
	 * as a defense-in-depth bound.
	 */
	private void tryRefillTier(@NotNull BossTier tier) {
		TierConfig tc = config.tiers.get(tier);
		if (tc == null || tc.minActive <= 0) {
			return;
		}
		// Cheap off-thread early-out; the authoritative check is inside the closure below.
		if (tierCounts.get(tier).get() >= tc.minActive) {
			return;
		}
		BossFabricModule mod = BossFabricModule.instance();
		MinecraftServer s = mod != null ? mod.getServer() : null;
		if (s == null) return;
		s.execute(() -> {
			List<ServerPlayer> online = s.getPlayerList().getPlayers();
			if (online.isEmpty()) {
				Logger.debug("Refill skipped for tier " + tier + ": no online players.");
				return;
			}
			BossSpawner spawner = mod.getBossSpawner();
			if (spawner == null) return;
			for (int attempt = 0; attempt < tc.minActive; attempt++) {
				// Authoritative recheck on main thread before each spawn.
				if (tierCounts.get(tier).get() >= tc.minActive) {
					return;
				}
				ServerPlayer anchor = online.get(RandomUtils.getRandom(0, online.size() - 1));
				Logger.info("Boss tier " + tier + " refill " + (attempt + 1) + "/" + tc.minActive
						+ " near " + anchor.getGameProfile().getName());
				BossSpawner.AdminSpawnResult res = spawner.systemRefillSpawn(anchor, tier);
				if (res.status() != BossSpawner.AdminSpawnStatus.SUCCESS) {
					Logger.warn("Refill spawn for tier " + tier + " failed: " + res.status()
							+ " (stopping further attempts this pass)");
					return;
				}
			}
		});
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
			if (entity == null || entity.isRemoved()) {
				pendingDespawns.remove(uuid);
				cleanup(uuid, CleanupKind.AUTO_DESPAWN, null, null);
			} else if (!entity.isBusy()) {
				pendingDespawns.remove(uuid);
				cleanup(uuid, CleanupKind.AUTO_DESPAWN, null, null);
			}
		}
	}

	/* ---------- Admin despawn router ---------- */

	/** Outcome of a single boss despawn request — used by callers to pick the admin reply. */
	public enum DespawnOutcome { CLEANED, QUEUED_BATTLE }

	/**
	 * Per-boss dispatch. Queues via {@link #pendingDespawns} when the boss is in battle
	 * (discarding mid-battle would corrupt Cobblemon's battle state); otherwise cleans up
	 * synchronously. Callers iterate {@link #getAllActive()} themselves for fan-out.
	 */
	public DespawnOutcome dispatchDespawn(@NotNull ActiveBoss boss) {
		PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
		Logger.info("dispatchDespawn " + shortId(boss.pokemonUUID()) + " — entity=" + (entity != null)
				+ " removed=" + (entity != null && entity.isRemoved())
				+ " busy=" + (entity != null && entity.isBusy()));
		if (entity != null && entity.isBusy()) {
			pendingDespawns.add(boss.pokemonUUID());
			return DespawnOutcome.QUEUED_BATTLE;
		}
		cleanup(boss.pokemonUUID(), CleanupKind.AUTO_DESPAWN, null, "admin despawn");
		return DespawnOutcome.CLEANED;
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
		// Prefer IUser for nickname-aware username (matches gyms convention).
		IUser user = IUser.getByUUID(winner.getUUID());
		String username = user != null ? user.getUsername() : winner.getGameProfile().getName();
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
			for (String cmd : commands) {
				try {
					BossFabricModule.instance().executeCommand(cmd);
				} catch (Throwable t) {
					Logger.warn("Reward command failed for boss " + boss.pokemonUUID()
							+ " (tier=" + boss.tier() + ", species=" + boss.species()
							+ ", winner=" + winner.getUUID() + "): " + cmd
							+ " — " + t.getClass().getSimpleName() + ": " + t.getMessage());
				}
			}
			String display = reward.getDisplayName() != null && !reward.getDisplayName().isBlank()
					? reward.getDisplayName()
					: extractDisplayLabel(commands);
			rolledEntries.add(quantity + "x " + display);
		}

		sendToWinner(winner, config.lang.bossRewardWinnerSummary
				.parse("glow_color", glow)
				.parse("rewards", String.join(", ", rolledEntries))
				.parse());
	}

	private void sendToWinner(@NotNull ServerPlayer winner, @NotNull String parsedMiniMessage) {
		winner.sendSystemMessage(BossFabricModule.instance().getMiniMessageManager().parse(parsedMiniMessage));
	}

	/**
	 * Best-effort human label from a reward command list. Picks the first non-placeholder
	 * token that contains a ':' or looks item-shaped — usually the item id
	 * ({@code cobblemon:rare_candy}) or balance currency ({@code pokecoins}). Last-resort
	 * fallback is just "reward".
	 */
	private static String extractDisplayLabel(@NotNull List<String> commands) {
		if (commands.isEmpty()) return "reward";
		// Look for a namespaced item id (namespace:slug) in the first command — that's the
		// only pattern we can infer reliably from. Other shapes (give_group <key>, balance
		// add <currency>, plushie give_class ...) should set BossReward.displayName explicitly.
		for (String token : commands.get(0).split("\\s+")) {
			if (token.startsWith("{") || !token.contains(":")) continue;
			String slug = token.substring(token.indexOf(':') + 1);
			return titleCase(slug.replace('_', ' '));
		}
		return "reward";
	}

	private static final Set<String> ACRONYM_TOKENS = Set.of("xl", "xs", "ev", "iv", "hp", "pp");

	/**
	 * Title-case each space-separated word; preserve known acronyms in upper case.
	 * "rare candy" → "Rare Candy", "exp candy xl" → "Exp Candy XL".
	 */
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
		if (team == null) {
			team = sb.addPlayerTeam(name);
		}
		// Always set color — covers (a) fresh team, (b) reload with stale team from prior config,
		// (c) admin reloaded config mid-session. Without this, recolors silently fail.
		ChatFormatting color = tc.glowChatFmt != null ? tc.glowChatFmt : ChatFormatting.WHITE;
		team.setColor(color);
		sb.addPlayerToTeam(entity.getScoreboardName(), team);
		entity.setGlowingTag(true);
		Logger.debug("Applied boss team " + name + " color=" + color + " glow=true to entity "
				+ entity.getScoreboardName() + " (tier=" + tc.displayName + ", shiny=" + entity.getPokemon().getShiny() + ")");
	}

	public void removeBossTeam(@NotNull MinecraftServer server, @NotNull UUID pokemonUUID) {
		ServerScoreboard sb = server.getScoreboard();
		PlayerTeam team = sb.getPlayerTeam(teamName(pokemonUUID));
		if (team != null) {
			sb.removePlayerTeam(team);
		}
	}

	/* ---------- Announce helper ---------- */

	private void announceDefeat(@NotNull ActiveBoss boss, @NotNull ServerPlayer winner) {
		TierConfig tc = config.tiers.get(boss.tier());
		gg.mmorealms.module.boss.common.AnnounceLevel lvl = tc.announceOnDefeat;
		if (lvl == null || lvl == gg.mmorealms.module.boss.common.AnnounceLevel.OFF) {
			return;
		}
		IUser user = IUser.getByUUID(winner.getUUID());
		String username = user != null ? user.getUsername() : winner.getGameProfile().getName();
		String glow = tc.glowColor.toLowerCase();

		switch (lvl) {
			case WORLD_CHAT -> {
				String message = config.lang.bossDefeatedAnnouncementWorld
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", boss.species())
						.parse("player", username)
						.parse();
				net.minecraft.network.chat.Component comp = BossFabricModule.instance()
						.getMiniMessageManager().parse(message);
				for (ServerPlayer p : winner.serverLevel().players()) {
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
