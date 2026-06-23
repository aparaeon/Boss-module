package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.boss.common.BossTierTheme;
import gg.mmorealms.module.chat.common.dto.GlobalMessageEvent;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.BattleWonEvent;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import java.util.HashSet;
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
	// Boss pokemonUUID -> UUID of the admin who queued its despawn, so we can confirm "DESPAWNED" once the battle ends.
	private final Map<UUID, UUID> pendingDespawnRequesters = new ConcurrentHashMap<>();
	private final Set<BossTier> refillRetryScheduled = ConcurrentHashMap.newKeySet();
	private @Nullable CancelableTimeTask pendingDespawnSweepTask;
	private final PendingDiscards pendingDiscards;

	private final AtomicLong lastBootstrapAt = new AtomicLong(0);

	public record ActiveBoss(
			UUID pokemonUUID,
			UUID entityUUID,
			BossTier tier,
			String species,
			int level,
			long spawnedAtEpoch,
			boolean systemSpawned,
			BlockPos spawnPos,
			ResourceLocation spawnDimension
	) {}

	public static final class NbtKeys {
		private NbtKeys() {}

		public static final int SCHEMA_VERSION = 2;

		public static final String BOSS = "mmo_realms_boss";
		public static final String SCHEMA = "mmo_realms_boss_schema_version";
		public static final String TIER = "mmo_realms_boss_tier";
		public static final String SPECIES = "mmo_realms_boss_species";
		public static final String LEVEL = "mmo_realms_boss_level";
		public static final String SPAWNED_AT = "mmo_realms_boss_spawned_at";
		public static final String SYSTEM_SPAWNED = "mmo_realms_boss_system_spawned";
		public static final String SPAWN_X = "mmo_realms_boss_spawn_x";
		public static final String SPAWN_Y = "mmo_realms_boss_spawn_y";
		public static final String SPAWN_Z = "mmo_realms_boss_spawn_z";
		public static final String SPAWN_DIMENSION = "mmo_realms_boss_spawn_dim";
	}

	public static class PendingDiscards {
		public Set<UUID> entityUUIDs = new HashSet<>();
	}

	public BossManager(BossConfig config, FileManager fileManager) {
		this.config = config;
		this.fileManager = fileManager;
		this.pendingDiscards = fileManager.load(PendingDiscards.class, "", "boss_pending_discards.json");
		for (BossTier tier : BossTier.values()) {
			tierCounts.put(tier, new AtomicInteger(0));
		}
	}

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

	public boolean registerActive(@NotNull ActiveBoss boss) {
		return active.putIfAbsent(boss.pokemonUUID(), boss) == null;
	}

	public @Nullable ActiveBoss get(@NotNull UUID pokemonUUID) {
		return active.get(pokemonUUID);
	}

	public boolean hasPendingDespawns() {
		return !pendingDespawns.isEmpty();
	}

	public java.util.Collection<ActiveBoss> getAllActive() {
		return active.values();
	}

	public @Nullable ActiveBoss findByShortId(@NotNull String shortId) {
		if (shortId.length() != 8) return null;
		String lower = shortId.toLowerCase();
		return active.values().stream()
				.filter(boss -> boss.pokemonUUID().toString().toLowerCase().startsWith(lower))
				.findFirst()
				.orElse(null);
	}

	public static String shortId(@NotNull UUID pokemonUUID) {
		return pokemonUUID.toString().substring(0, 8);
	}

	public void rollbackRegistration(@NotNull UUID pokemonUUID) {
		active.remove(pokemonUUID);
		cancelTasks(pokemonUUID);
	}

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
		TierConfig.EffectConfig amb = tc.ambientEffect;
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

	private void emitAmbient(@NotNull ActiveBoss boss, @NotNull TierConfig.EffectConfig amb) {
		BossFabricModule.instance().runOnMain(() -> {
			PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
			if (entity == null || entity.isRemoved()) {
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
		pendingDespawnRequesters.remove(pokemonUUID);
	}

	public enum CleanupKind {
		DEFEAT,
		DEFEAT_NO_REWARD,
		AUTO_DESPAWN
	}

	public void handleDefeat(@NotNull UUID pokemonUUID, @NotNull ServerPlayer winner) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT, winner, null, null);
	}

	public void handleDefeatWithoutReward(@NotNull UUID pokemonUUID, @Nullable UUID winnerUUID, @NotNull String reason) {
		cleanup(pokemonUUID, CleanupKind.DEFEAT_NO_REWARD, null, winnerUUID, reason);
	}

	public void handleBattleWon(@NotNull BattleWonEvent event) {
		ActiveBoss defeatedBoss = findBossIn(event.getLosers());
		if (defeatedBoss != null) {
			UUID winnerUuid = findFirstPlayerUuidIn(event.getWinners());
			Logger.debug("BattleWon: detected boss defeat uuid=" + defeatedBoss.pokemonUUID()
					+ " winner=" + winnerUuid);
			if (winnerUuid != null) {
				MinecraftServer server = BossFabricModule.instance().getServer();
				ServerPlayer winner = server != null ? server.getPlayerList().getPlayer(winnerUuid) : null;
				if (winner == null) {
					handleDefeatWithoutReward(defeatedBoss.pokemonUUID(), winnerUuid,
							"winner " + winnerUuid + " disconnected before reward dispatch");
					return;
				}
				handleDefeat(defeatedBoss.pokemonUUID(), winner);
			} else {
				handleDefeatWithoutReward(defeatedBoss.pokemonUUID(), null, "no player on winning side");
			}
			return;
		}

		ActiveBoss victoriousBoss = findBossIn(event.getWinners());
		if (victoriousBoss == null) {
			return;
		}
		TierConfig tc = config.tiers.get(victoriousBoss.tier());
		if (tc == null || tc.defeatDialogue == null || tc.defeatDialogue.isEmpty()) {
			return;
		}
		List<ServerPlayer> losers = findPlayerTargets(event.getLosers());
		for (ServerPlayer loser : losers) {
			sendBossReaction(loser, victoriousBoss);
		}
	}

	public @Nullable ActiveBoss findBossIn(@Nullable Iterable<BattleActor> actors) {
		if (actors == null) return null;
		for (BattleActor actor : actors) {
			if (!(actor instanceof com.cobblemon.mod.common.battles.actor.PokemonBattleActor pokeActor)) continue;
			UUID uuid = pokeActor.getPokemon().getOriginalPokemon().getUuid();
			ActiveBoss boss = active.get(uuid);
			if (boss != null) {
				return boss;
			}
		}
		return null;
	}

	public boolean consumePendingDiscard(@NotNull UUID entityUUID) {
		if (!pendingDiscards.entityUUIDs.remove(entityUUID)) {
			return false;
		}
		savePendingDiscards();
		return true;
	}

	private void savePendingDiscards() {
		fileManager.writeFile("", "boss_pending_discards.json", BossFabricModule.instance().toJson(pendingDiscards));
	}

	public void bootstrapFillAllTiers() {
		long now = System.currentTimeMillis();
		long previous = lastBootstrapAt.get();
		if (now - previous < BOOTSTRAP_DEBOUNCE_MS || !lastBootstrapAt.compareAndSet(previous, now)) {
			return;
		}
		Logger.info("Bootstrap: scheduling staggered refill checks for all tiers");
		BossTier[] tiers = BossTier.values();
		for (int i = 0; i < tiers.length; i++) {
			BossTier tier = tiers[i];
			ScheduleUtils.runTaskLater(() -> tryRefillTier(tier), Time.seconds((i + 1) * 5L));
		}
	}

	public void refillSweep() {
		for (BossTier tier : BossTier.values()) {
			tryRefillTier(tier);
		}
	}

	private void cleanup(@NotNull UUID pokemonUUID, @NotNull CleanupKind kind,
	                     @Nullable ServerPlayer winner, @Nullable UUID offlineWinnerUUID, @Nullable String reason) {
		ActiveBoss boss = active.remove(pokemonUUID);
		if (boss == null) {
			Logger.debug("Boss cleanup [" + kind + "] skipped — UUID " + pokemonUUID
					+ " already removed by another path.");
			return;
		}
		Logger.info("Boss cleanup [" + kind + "] " + boss.tier() + " " + boss.species()
				+ " lv." + boss.level() + " (" + shortId(pokemonUUID) + ")"
				+ (boss.systemSpawned() ? " [system]" : " [admin]")
				+ (winner != null ? " winner=" + winner.getGameProfile().getName() : ""));
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

		if (entity != null) {
			entity.getPokemon().getPersistentData().putBoolean(NbtKeys.BOSS, false);
		} else {
			pendingDiscards.entityUUIDs.add(boss.entityUUID());
			savePendingDiscards();
			Logger.info("Boss entity " + boss.entityUUID() + " not loaded at cleanup [" + kind
					+ "]; flagged for hard-despawn on next chunk load.");
		}

		if (kind == CleanupKind.DEFEAT && winner != null) {
			TierConfig tc = config.tiers.get(boss.tier());
			sendVictoryTitle(winner, boss);
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

		if (boss.systemSpawned()) {
			tryRefillTier(boss.tier());
		}
	}

	private void sendPersonalDefeat(@NotNull ServerPlayer winner, @NotNull ActiveBoss boss, @NotNull TierConfig tc) {
		sendToWinner(winner, config.lang.bossPersonalDefeat
				.parse("tier_display", tc.displayName)
				.parse("species", speciesDisplayName(boss.species()))
				.parse());
	}

	public void sendBossReaction(@NotNull ServerPlayer player, @NotNull ActiveBoss boss) {
		TierConfig tc = config.tiers.get(boss.tier());
		if (tc == null || tc.defeatDialogue == null || tc.defeatDialogue.isEmpty()) {
			return;
		}
		List<String> box = tc.defeatDialogue.get(RandomUtils.getRandom(0, tc.defeatDialogue.size() - 1));
		if (box == null || box.isEmpty()) {
			return;
		}
		String subtitle = buildReactionSubtitle(boss.tier(), box);
		sendTitlePopup(player, BossTierTheme.bossReactionTitle(boss.tier()), subtitle);
	}

	public void sendEncounterPopup(@NotNull ServerPlayer player, @NotNull BossTier tier, @NotNull String speciesDisplay, int level) {
		sendTitlePopup(player, BossTierTheme.encounterTitle(tier, speciesDisplay, level), "");
	}

	public void sendBattleCry(@NotNull ServerPlayer player, @NotNull BossTier tier, @NotNull String speciesDisplay) {
		TierConfig tc = config.tiers.get(tier);
		if (tc == null || tc.battleCryTaunts == null || tc.battleCryTaunts.isEmpty()) {
			return;
		}
		String taunt = tc.battleCryTaunts.get(RandomUtils.getRandom(0, tc.battleCryTaunts.size() - 1))
				.replace("{species}", speciesDisplay);
		sendTitlePopup(player, BossTierTheme.battleCryTitle(tier), taunt);
	}

	private void sendVictoryTitle(@NotNull ServerPlayer winner, @NotNull ActiveBoss boss) {
		String speciesDisplay = speciesDisplayName(boss.species());
		VictoryScreen screen = victoryScreenFor(boss.tier());
		if (screen == null) {
			return;
		}
		sendTitlePopup(winner, screen.title().parse(), buildVictorySubtitle(screen.subtitles(), speciesDisplay));
	}

	private void sendTitlePopup(@NotNull ServerPlayer winner, @NotNull String title, @NotNull String subtitle) {
		var miniMessage = BossFabricModule.instance().getMiniMessageManager();
		winner.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
		winner.connection.send(new ClientboundSetTitleTextPacket(miniMessage.parse(title)));
		winner.connection.send(new ClientboundSetSubtitleTextPacket(miniMessage.parse(subtitle)));
	}

	private @Nullable VictoryScreen victoryScreenFor(@NotNull BossTier tier) {
		return switch (tier) {
			case LEGENDARY -> new VictoryScreen(config.lang.bossVictoryLegendaryTitle, config.lang.bossVictoryLegendarySubtitles);
			case MEGA -> new VictoryScreen(config.lang.bossVictoryMegaTitle, config.lang.bossVictoryMegaSubtitles);
			case MYTHICAL -> new VictoryScreen(config.lang.bossVictoryMythicalTitle, config.lang.bossVictoryMythicalSubtitles);
			default -> null;
		};
	}

	private static String buildVictorySubtitle(@NotNull List<String> lines, @NotNull String speciesDisplay) {
		// One short line per popup — concatenating every line overflows the subtitle and clips at the screen edge.
		String line = lines.get(RandomUtils.getRandom(0, lines.size() - 1));
		return line.replace("{species}", speciesDisplay);
	}

	private static String buildReactionSubtitle(@NotNull BossTier tier, @NotNull List<String> lines) {
		String line = lines.get(RandomUtils.getRandom(0, lines.size() - 1));
		return BossTierTheme.wrapTierGradient(tier, line);
	}

	private @NotNull List<ServerPlayer> findPlayerTargets(@Nullable List<BattleActor> actors) {
		MinecraftServer server = BossFabricModule.instance().getServer();
		if (server == null || actors == null) {
			return List.of();
		}
		List<ServerPlayer> players = new ArrayList<>();
		for (BattleActor actor : actors) {
			if (!(actor instanceof com.cobblemon.mod.common.battles.actor.PlayerBattleActor playerActor)) {
				continue;
			}
			ServerPlayer player = server.getPlayerList().getPlayer(playerActor.getUuid());
			if (player != null) {
				players.add(player);
			}
		}
		return players;
	}

	private @Nullable UUID findFirstPlayerUuidIn(@Nullable List<BattleActor> actors) {
		if (actors == null) return null;
		for (BattleActor actor : actors) {
			if (actor instanceof com.cobblemon.mod.common.battles.actor.PlayerBattleActor playerActor) {
				return playerActor.getUuid();
			}
		}
		return null;
	}

	private record VictoryScreen(@NotNull MessageBuilder title, @NotNull List<String> subtitles) {}

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
			List<ServerPlayer> anchors = new ArrayList<>(s.getPlayerList().getPlayers().stream()
					.filter(player -> allowedDims.contains(player.serverLevel().dimension().location().toString()))
					.toList());
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
		List<UUID> snapshot = new ArrayList<>(pendingDespawns);
		for (UUID uuid : snapshot) {
			ActiveBoss boss = active.get(uuid);
			if (boss == null) {
				pendingDespawns.remove(uuid);
				pendingDespawnRequesters.remove(uuid);
				continue;
			}
			PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
			if (entity == null || entity.isRemoved() || !entity.isBusy()) {
				pendingDespawns.remove(uuid);
				// Capture before cleanup — cleanup() -> cancelTasks() clears the requester entry.
				UUID requester = pendingDespawnRequesters.get(uuid);
				cleanup(uuid, CleanupKind.AUTO_DESPAWN, null, null, null);
				notifyDespawnRequester(requester, boss);
			}
		}
	}

	private void notifyDespawnRequester(@Nullable UUID requesterUUID, @NotNull ActiveBoss boss) {
		if (requesterUUID == null) {
			return;
		}
		MinecraftServer server = BossFabricModule.instance().getServer();
		ServerPlayer requester = server != null ? server.getPlayerList().getPlayer(requesterUUID) : null;
		if (requester == null) {
			return;
		}
		BossFabricModule.instance().sendLang(requester, config.lang.adminDespawnSuccess
				.parse("tier", boss.tier().name())
				.parse("species", boss.species())
				.parse("short_id", shortId(boss.pokemonUUID())));
	}

	public enum DespawnOutcome { CLEANED, QUEUED_BATTLE }

	public DespawnOutcome dispatchDespawn(@NotNull ActiveBoss boss) {
		return dispatchDespawn(boss, null);
	}

	public DespawnOutcome dispatchDespawn(@NotNull ActiveBoss boss, @Nullable UUID requesterUUID) {
		PokemonEntity entity = BossFabricModule.instance().findEntity(boss.entityUUID());
		Logger.info("dispatchDespawn " + shortId(boss.pokemonUUID()) + " — entity=" + (entity != null)
				+ " removed=" + (entity != null && entity.isRemoved())
				+ " busy=" + (entity != null && entity.isBusy()));
		if (entity != null && entity.isBusy()) {
			pendingDespawns.add(boss.pokemonUUID());
			if (requesterUUID != null) {
				pendingDespawnRequesters.put(boss.pokemonUUID(), requesterUUID);
			}
			startPendingDespawnSweep();
			return DespawnOutcome.QUEUED_BATTLE;
		}
		cleanup(boss.pokemonUUID(), CleanupKind.AUTO_DESPAWN, null, null, "admin despawn");
		return DespawnOutcome.CLEANED;
	}

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
			TierConfig.BossReward reward = RandomUtils.getRandomWeighed(tc.rewards);
			int quantity = Math.max(1, (int) RandomUtils.getRandom(reward.getQuantity()));
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
			if (executeRewardCommands(boss, winner, commands)) {
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

	private boolean executeRewardCommands(@NotNull ActiveBoss boss, @NotNull ServerPlayer winner, @NotNull List<String> commands) {
		boolean succeeded = true;
		for (String cmd : commands) {
			try {
				if (!executeRewardCommand(cmd)) {
					succeeded = false;
					Logger.warn("Reward command returned no success for boss " + boss.pokemonUUID()
							+ " (tier=" + boss.tier() + ", species=" + boss.species()
							+ ", winner=" + winner.getUUID() + "): " + cmd);
				}
			} catch (CommandSyntaxException | RuntimeException t) {
				succeeded = false;
				Logger.warn("Reward command failed for boss " + boss.pokemonUUID()
						+ " (tier=" + boss.tier() + ", species=" + boss.species()
						+ ", winner=" + winner.getUUID() + "): " + cmd
						+ " — " + t.getClass().getSimpleName() + ": " + t.getMessage());
			}
		}
		return succeeded;
	}

	private boolean executeRewardCommand(@NotNull String cmd) throws CommandSyntaxException {
		MinecraftServer server = BossFabricModule.instance().getServer();
		if (server == null) {
			return false;
		}
		return server.getCommands().getDispatcher().execute(cmd, server.createCommandSourceStack()) > 0;
	}

	private static String usernameOf(@NotNull ServerPlayer player) {
		IUser user = IUser.getByUUID(player.getUUID());
		return user != null ? user.getUsername() : player.getGameProfile().getName();
	}

	private void sendToWinner(@NotNull ServerPlayer winner, @NotNull String parsedMiniMessage) {
		winner.sendSystemMessage(BossFabricModule.instance().getMiniMessageManager().parse(parsedMiniMessage));
	}

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
		if (!freshTeam && (colorFixed || memberFixed || glowFixed)) {
			Logger.info("Boss glow drift repaired on " + scoreboardName + " (team=" + name + "):"
					+ (colorFixed ? " color" : "") + (memberFixed ? " membership" : "") + (glowFixed ? " glowFlag" : ""));
		}
	}

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

	private void announceDefeat(@NotNull ActiveBoss boss, @NotNull String username, @Nullable ServerLevel worldLevel) {
		TierConfig tc = config.tiers.get(boss.tier());
		TierConfig.AnnounceLevel lvl = tc.announceOnDefeat;
		if (lvl == null || lvl == TierConfig.AnnounceLevel.OFF) {
			return;
		}
		String glow = tc.glowColor.toLowerCase();
		String speciesDisplay = speciesDisplayName(boss.species());

		switch (lvl) {
			case WORLD_CHAT -> {
				if (worldLevel == null) {
					return;
				}
				String message = config.lang.bossDefeatedAnnouncementWorld
						.parse("announcement_title", BossTierTheme.title(boss.tier(), BossTierTheme.BannerKind.DEFEAT))
						.parse("announcement_line", BossTierTheme.defeatLine(boss.tier(), speciesDisplay, username))
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", speciesDisplay)
						.parse("species_display", speciesDisplay)
						.parse("player", username)
						.parse();
				sendWorldChat(worldLevel, message);
			}
			case GLOBAL_CHAT -> {
				String message = config.lang.bossDefeatedAnnouncementGlobal
						.parse("announcement_title", BossTierTheme.title(boss.tier(), BossTierTheme.BannerKind.DEFEAT))
						.parse("announcement_line", BossTierTheme.defeatLine(boss.tier(), speciesDisplay, username))
						.parse();
				sendGlobalChat(message);
			}
			default -> {}
		}
	}

	static void sendWorldChat(@NotNull ServerLevel worldLevel, @NotNull String message) {
		net.minecraft.network.chat.Component comp = BossFabricModule.instance()
				.getMiniMessageManager().parse(message);
		for (ServerPlayer p : worldLevel.players()) {
			p.sendSystemMessage(comp);
		}
	}

	static void sendGlobalChat(@NotNull String message) {
		new GlobalMessageEvent(message).send();
	}

	static String speciesDisplayName(@NotNull String species) {
		com.cobblemon.mod.common.pokemon.Species resolved = com.cobblemon.mod.common.api.pokemon.PokemonSpecies.INSTANCE.getByName(species);
		if (resolved != null) {
			return BossTierTheme.capitalizeFirst(resolved.getTranslatedName().getString());
		}
		return BossTierTheme.capitalizeFirst(species);
	}
}
