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
			BossFabricModule.instance().runOnMain(() -> queueOrAutoDespawn(boss));
			return;
		}
		CancelableTimeTask task = ScheduleUtils.runTaskLater(
				() -> BossFabricModule.instance().runOnMain(() -> queueOrAutoDespawn(boss)),
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

	public void cancelTasks(@NotNull UUID pokemonUUID) {
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
		/** Boss died to environment (lava/fall/drown) — Cobblemon already faints, no rewards, no announcement, no discard (entity removal handled by faint). */
		ENV_FAINT,
		/** Despawn timer / admin despawn — discard entity, no rewards/announcement. */
		AUTO_DESPAWN
	}

	public void handleDefeat(@NotNull UUID pokemonUUID, @NotNull ServerPlayer winner) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT, winner, null);
	}

	public void handleDefeatWithoutReward(@NotNull UUID pokemonUUID, @NotNull String reason) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT_NO_REWARD, null, reason);
	}

	public void handleEnvironmentalFaint(@NotNull UUID pokemonUUID) {
		cleanup(pokemonUUID, CleanupKind.ENV_FAINT, null, null);
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
	 *   7. discard entity (all kinds except ENV_FAINT — Cobblemon already faints the entity).
	 *   8. announceDefeat (DEFEAT only).
	 *
	 * Accepted crash-edge: clearing only {@code mmo_realms_boss=false} leaves residual state on the
	 * entity — custom name, glow tag, scale, and {@code UncatchableProperty} all persist via
	 * Cobblemon's codec. After a crash between marker-clear and entity.discard, that Pokemon reloads
	 * with no boss cache entry, no anti-flee mixin engagement (mmo_realms_boss=false), no despawn
	 * timer, no reward path — but the player still sees a boss-shaped, uncatchable, never-cleaned-up
	 * wild Pokemon. No economic exploit (no double rewards, no re-fight payout). Cosmetic + janitorial
	 * drift only. Deeper cleanup (clearing UncatchableProperty + scale + custom name) is a v2 if
	 * staff start seeing these in the wild.
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
				+ (winner != null ? " winner=" + winner.getGameProfile().getName() : ""));
		releaseTier(boss.tier());
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
		} else if (kind != CleanupKind.ENV_FAINT) {
			Logger.warn("Boss entity " + boss.entityUUID() + " not found at cleanup [" + kind
					+ "]; cannot clear mmo_realms_boss NBT — chunk reload may re-register this boss.");
		}

		if (kind == CleanupKind.DEFEAT && winner != null) {
			dispatchRewards(boss, winner);
		}

		if (entity != null && kind != CleanupKind.ENV_FAINT) {
			entity.discard();
		}

		if (kind == CleanupKind.DEFEAT && winner != null) {
			announceDefeat(boss, winner);
		}

		if (reason != null) {
			Logger.info("Boss " + boss.tier() + " " + boss.species() + " (" + pokemonUUID
					+ ") cleanup [" + kind + "]: " + reason);
		}

		// Immediate one-shot refill if the tier dropped below its floor.
		tryRefillTier(boss.tier());
	}

	/**
	 * Backend-local refill — when a system-spawned boss is removed and the tier active count
	 * drops below {@link TierConfig#minActive}, fire one spawn attempt locally.
	 * Skips when the floor is 0 (refill disabled) or no online players exist.
	 */
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
		List<ServerPlayer> online = s.getPlayerList().getPlayers();
		if (online.isEmpty()) {
			Logger.debug("Refill skipped for tier " + tier + ": no online players.");
			return;
		}
		ServerPlayer anchor = online.get(RandomUtils.getRandom(0, online.size() - 1));
		BossSpawner spawner = mod.getBossSpawner();
		if (spawner == null) return;
		Logger.info("Boss tier " + tier + " dropped below minActive (" + tc.minActive
				+ "); firing immediate refill spawn near " + anchor.getGameProfile().getName());
		// adminSpawn uses forceReserveTier (bypasses cap) — fine because we already checked the floor.
		// Result is logged on failure but otherwise fire-and-forget.
		s.execute(() -> {
			BossSpawner.AdminSpawnResult res = spawner.adminSpawn(anchor, tier, null, false, null, null);
			if (res.status() != BossSpawner.AdminSpawnStatus.SUCCESS) {
				Logger.warn("Refill spawn for tier " + tier + " failed: " + res.status());
			}
		});
	}

	private void queueOrAutoDespawn(@NotNull ActiveBoss boss) {
		PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
		if (entity == null || entity.isRemoved()) {
			// Entity already gone — clean cache state via consolidated cleanup.
			cleanup(boss.pokemonUUID(), CleanupKind.AUTO_DESPAWN, null, null);
			return;
		}
		if (entity.isBusy()) {
			// Boss is in active battle; defer despawn until battle ends.
			// Mirrors LegendaryDespawnManager.failedDespawns pattern. Retry fires on next battle-end event.
			pendingDespawns.add(boss.pokemonUUID());
			return;
		}
		cleanup(boss.pokemonUUID(), CleanupKind.AUTO_DESPAWN, null, null);
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

	/**
	 * Admin-initiated despawn — always {@code force=true} via the command/network paths.
	 * Force mode bypasses the {@code isBusy()} gate so a boss near the admin (or otherwise
	 * "busy" per Cobblemon's entity AI) is removed immediately instead of being queued
	 * for the next battle-end sweep.
	 * <p>
	 * When {@code force=false}, behaviour is the legacy timer-driven path: in-battle bosses
	 * are queued and retried by {@link #retryPendingDespawns()} on {@code BattleWonEvent}.
	 */
	public int handleDespawnRequest(@Nullable UUID specific, @Nullable BossTier tierFilter,
	                                boolean despawnAll, boolean force) {
		int count = 0;
		if (specific != null) {
			ActiveBoss boss = active.get(specific);
			if (boss != null) {
				dispatchDespawn(boss, force);
				count++;
			}
		} else if (tierFilter != null) {
			for (ActiveBoss boss : active.values()) {
				if (boss.tier() == tierFilter) {
					dispatchDespawn(boss, force);
					count++;
				}
			}
		} else if (despawnAll) {
			for (ActiveBoss boss : active.values()) {
				dispatchDespawn(boss, force);
				count++;
			}
		}
		return count;
	}

	private void dispatchDespawn(@NotNull ActiveBoss boss, boolean force) {
		if (force) {
			cleanup(boss.pokemonUUID(), CleanupKind.AUTO_DESPAWN, null,
					"admin force despawn (bypassed isBusy gate)");
		} else {
			queueOrAutoDespawn(boss);
		}
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
		}
	}

	/* ---------- Scoreboard team (single source of truth across spawn + reload) ---------- */

	public static String teamName(@NotNull UUID pokemonUUID) {
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
			case TITLE -> {
				String chat = config.lang.bossDefeatedAnnouncementTitleChat
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", boss.species())
						.parse("player", username)
						.parse();
				String title = config.lang.bossDefeatedAnnouncementTitleMain
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", boss.species())
						.parse("player", username)
						.parse();
				String sub = config.lang.bossDefeatedAnnouncementTitleSub
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", boss.species())
						.parse("player", username)
						.parse();
				new gg.mmorealms.module.boss.common.event.BossTitleAnnouncementEvent(
						chat, title, sub, tc.titleSound
				).send();
			}
			default -> {}
		}
	}
}
