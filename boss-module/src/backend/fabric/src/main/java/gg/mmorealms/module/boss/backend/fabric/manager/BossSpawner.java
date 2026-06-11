package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.boss.common.event.BossSpawnEvent;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPokemon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BossSpawner {

	private final BossConfig config;
	private final BossManager manager;

	public BossSpawner(BossConfig config, BossManager manager) {
		this.config = config;
		this.manager = manager;
	}

	public void handleSpawnRequest(@NotNull BossSpawnEvent ev) {
		MinecraftServer server = BossFabricModule.instance().getServer();
		if (server == null) {
			Logger.warn("BossSpawnEvent received but server is null");
			return;
		}
		server.execute(() -> processSpawnFromEvent(server, ev));
	}

	/** Backend admin entry point. Synchronous; admin is local — no network event. */
	public AdminSpawnResult adminSpawn(
			@NotNull ServerPlayer anchor,
			@NotNull BossTier tier,
			@Nullable String species,
			boolean shiny,
			@Nullable Integer level,
			@Nullable BlockPos forcedPos
	) {
		MinecraftServer server = anchor.getServer();
		if (server == null) return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);

		TierConfig tc = config.tiers.get(tier);
		if (tc == null) return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);

		// Cobblemon's getByName throws on uppercase path chars — normalize before any registry lookup.
		species = normalizeSpecies(species);
		if (species != null && PokemonSpecies.INSTANCE.getByName(species) == null) {
			return AdminSpawnResult.of(AdminSpawnStatus.SPECIES_NOT_FOUND);
		}
		if (species != null && !speciesInTierPool(tier, tc, species)) {
			return AdminSpawnResult.of(AdminSpawnStatus.SPECIES_NOT_IN_POOL);
		}
		if (level != null && (level < tc.levelRange.getMin() || level > tc.levelRange.getMax())) {
			return AdminSpawnResult.of(AdminSpawnStatus.LEVEL_OUT_OF_RANGE);
		}

		// Admin spawns bypass the per-tier counter — they don't count toward maxActive or satisfy minActive.
		try {
			SpeciesPick pick = resolveSpecies(tier, tc, species, /*enforceTierPool=*/ true);
			if (pick == null) {
				return AdminSpawnResult.of(AdminSpawnStatus.SPECIES_NOT_FOUND);
			}
			int resolvedLevel = level != null ? level : (int) RandomUtils.getRandom(tc.levelRange);
			BlockPos position = (forcedPos != null) ? forcedPos : SpawnPositionFinder.find(anchor, tc, config);
			if (position == null) {
				return AdminSpawnResult.of(AdminSpawnStatus.POSITION_NOT_FOUND);
			}
			UUID spawnedUUID = spawnBoss(server, anchor, position, tier, tc, pick, resolvedLevel, shiny, /*systemSpawned=*/ false);
			if (spawnedUUID == null) {
				return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
			}
			return AdminSpawnResult.success(spawnedUUID, pick.species(), resolvedLevel,
					position.getX(), position.getY(), position.getZ());
		} catch (Throwable t) {
			Logger.error("Admin boss spawn pipeline failed for tier " + tier
					+ " — " + StackTraceUtils.toString(t));
			return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
		}
	}


	/** System refill spawn — called by {@link BossManager#tryRefillTier}. Cap-safe via {@code tryReserveTier}. */
	public AdminSpawnResult systemRefillSpawn(@NotNull ServerPlayer anchor, @NotNull BossTier tier) {
		MinecraftServer server = anchor.getServer();
		if (server == null) return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
		TierConfig tc = config.tiers.get(tier);
		if (tc == null) return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);

		if (!manager.tryReserveTier(tier)) {
			return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
		}
		boolean owned = false;
		try {
			SpeciesPick pick = resolveSpecies(tier, tc, null, /*enforceTierPool=*/ true);
			if (pick == null) return AdminSpawnResult.of(AdminSpawnStatus.SPECIES_NOT_FOUND);
			int level = (int) RandomUtils.getRandom(tc.levelRange);
			BlockPos position = SpawnPositionFinder.find(anchor, tc, config);
			if (position == null) return AdminSpawnResult.of(AdminSpawnStatus.POSITION_NOT_FOUND);
			UUID uuid = spawnBoss(server, anchor, position, tier, tc, pick, level, /*shiny=*/ false, /*systemSpawned=*/ true);
			owned = uuid != null;
			if (!owned) return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
			return AdminSpawnResult.success(uuid, pick.species(), level, position.getX(), position.getY(), position.getZ());
		} catch (Throwable t) {
			Logger.error("System refill spawn pipeline failed for tier " + tier
					+ " — " + StackTraceUtils.toString(t));
			return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
		} finally {
			if (!owned) {
				manager.releaseTier(tier);
			}
		}
	}

	public enum AdminSpawnStatus {
		SUCCESS,
		SPECIES_NOT_FOUND,
		SPECIES_NOT_IN_POOL,
		LEVEL_OUT_OF_RANGE,
		POSITION_NOT_FOUND,
		SPAWN_FAILED
	}

	public record AdminSpawnResult(@NotNull AdminSpawnStatus status,
	                               @Nullable UUID pokemonUUID,
	                               @Nullable String species,
	                               int level,
	                               int x, int y, int z) {
		public static AdminSpawnResult of(AdminSpawnStatus s) { return new AdminSpawnResult(s, null, null, 0, 0, 0, 0); }
		public static AdminSpawnResult success(UUID uuid, String species, int level, int x, int y, int z) {
			return new AdminSpawnResult(AdminSpawnStatus.SUCCESS, uuid, species, level, x, y, z);
		}
	}

	private void processSpawnFromEvent(@NotNull MinecraftServer server, @NotNull BossSpawnEvent ev) {
		TierConfig tc = config.tiers.get(ev.getTier());
		if (tc == null) {
			Logger.warn("BossSpawnEvent for unknown tier: " + ev.getTier());
			return;
		}

		// Re-filter eligiblePlayerUUIDs for online state (disconnect race guard).
		List<ServerPlayer> online = ev.getEligiblePlayerUUIDs().stream()
				.map(server.getPlayerList()::getPlayer)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		// Anchor: admin's online location preferred; else random eligible.
		ServerPlayer anchor = null;
		if (ev.getRequesterUUID() != null) {
			anchor = server.getPlayerList().getPlayer(ev.getRequesterUUID());
		}
		if (anchor == null && !online.isEmpty()) {
			anchor = online.get(RandomUtils.getRandom(0, online.size() - 1));
		}
		if (anchor == null) {
			Logger.warn("BossSpawnEvent: no online anchor player; aborting");
			return;
		}

		if (!manager.tryReserveTier(ev.getTier())) {
			Logger.info("Tier " + ev.getTier() + " is at cap (" + tc.maxActive + "); spawn aborted");
			return;
		}

		// spawnBoss owns world rollback; this finally only releases the tier counter on failure.
		boolean owned = false;
		try {
			SpeciesPick pick = resolveSpecies(ev.getTier(), tc, ev.getSpecies(), /*enforceTierPool=*/ true);
			if (pick == null) {
				Logger.warn("Could not resolve species for tier " + ev.getTier() + "; aborting");
				return;
			}

			int level = resolveLevel(tc, ev);
			if (level < 0) {
				return; // out of range — no clamp
			}

			BlockPos position = SpawnPositionFinder.find(anchor, tc, config);
			if (position == null) {
				Logger.warn("Could not find valid spawn position for " + pick.species()
						+ " near " + anchor.getName().getString());
				return;
			}

			owned = spawnBoss(server, anchor, position, ev.getTier(), tc, pick, level, ev.isShiny(), /*systemSpawned=*/ true) != null;
		} catch (Throwable t) {
			Logger.error("Boss spawn failed for tier " + ev.getTier()
					+ " — " + StackTraceUtils.toString(t));
		} finally {
			if (!owned) {
				manager.releaseTier(ev.getTier());
			}
		}
	}

	/** Resolved species + optional mega aspect (null for non-MEGA tiers). */
	private record SpeciesPick(String species, @Nullable String megaAspect) {}

	/** Lowercase/trim so Cobblemon's ResourceLocation-backed lookup can't throw on uppercase input. */
	private static @Nullable String normalizeSpecies(@Nullable String species) {
		if (species == null) return null;
		String normalized = species.trim().toLowerCase(Locale.ROOT);
		return normalized.isEmpty() ? null : normalized;
	}

	/** Unified species resolver. {@code enforceTierPool=true} rejects forced species outside the tier pool. */
	private @Nullable SpeciesPick resolveSpecies(@NotNull BossTier tier, @NotNull TierConfig tc,
	                                             @Nullable String forcedInput, boolean enforceTierPool) {
		String forced = normalizeSpecies(forcedInput);
		if (forced != null) {
			if (PokemonSpecies.INSTANCE.getByName(forced) == null) {
				return null; // unknown species
			}
			if (enforceTierPool && !speciesInTierPool(tier, tc, forced)) {
				return null;
			}
			// For MEGA tier, forced species needs its mega aspect or it spawns as base form.
			// Pick the first MegaEvolution entry matching the species (Charizard X+Y both qualify).
			String forcedAspect = null;
			if (tier == BossTier.MEGA) {
				forcedAspect = MegaEvolution.stream()
						.filter(me -> me.getSpeciesName() != null
								&& me.getSpeciesName().equalsIgnoreCase(forced))
						.findFirst()
						.map(MegaEvolution::getMegaAspect)
						.orElse(null);
				if (forcedAspect == null && enforceTierPool) {
					return null;
				}
			}
			return new SpeciesPick(forced, forcedAspect);
		}

		// MEGA tier: pick from megaCandidates, which gives us both species + aspect (never a base form).
		if (tier == BossTier.MEGA) {
			List<MegaEvolution> candidates = megaCandidates(tc);
			if (candidates.isEmpty()) {
				return null;
			}
			MegaEvolution chosen = candidates.get(RandomUtils.getRandom(0, candidates.size() - 1));
			return new SpeciesPick(chosen.getSpeciesName(), chosen.getMegaAspect());
		}

		// Default tier: union of PokemonClass pools + extraSpecies − excludedSpecies.
		List<String> pool = buildSpeciesPool(tc);
		if (pool.isEmpty()) {
			return null;
		}
		String picked = pool.get(RandomUtils.getRandom(0, pool.size() - 1));
		return new SpeciesPick(picked, null);
	}

	/** MEGA pool — only species with a valid mega aspect. {@code extraSpecies} is the allowlist when {@code includeAllMegaCapable=false}. */
	private @NotNull List<MegaEvolution> megaCandidates(@NotNull TierConfig tc) {
		return MegaEvolution.stream()
				.filter(me -> me.getSpeciesName() != null
						&& PokemonSpecies.INSTANCE.getByName(normalizeSpecies(me.getSpeciesName())) != null)
				.filter(me -> me.getMegaAspect() != null && !me.getMegaAspect().isEmpty())
				.filter(me -> tc.includeAllMegaCapable
						|| tc.extraSpecies.stream().anyMatch(extra -> extra.equalsIgnoreCase(me.getSpeciesName())))
				.collect(Collectors.toList());
	}

	private boolean speciesInTierPool(@NotNull BossTier tier, @NotNull TierConfig tc, @NotNull String species) {
		if (tier == BossTier.MEGA) {
			return megaCandidates(tc).stream()
					.anyMatch(me -> me.getSpeciesName().equalsIgnoreCase(species));
		}
		return buildSpeciesPool(tc).stream().anyMatch(s -> s.equalsIgnoreCase(species));
	}

	/** Pool for the autocomplete suggester — must mirror {@link #speciesInTierPool} so tab-complete never suggests a rejected species. */
	public @NotNull List<String> buildSpeciesPoolForTier(@NotNull BossTier tier, @NotNull TierConfig tc) {
		if (tier == BossTier.MEGA) {
			return megaCandidates(tc).stream()
					.map(MegaEvolution::getSpeciesName)
					.distinct()
					.collect(Collectors.toList());
		}
		return buildSpeciesPool(tc);
	}

	private @NotNull List<String> buildSpeciesPool(@NotNull TierConfig tc) {
		Set<String> excluded = new HashSet<>(tc.excludedSpecies);
		Set<String> union = new HashSet<>();

		// PokemonClass species lists live in pokemon-module's shared config.
		if (tc.pokemonClasses != null && !tc.pokemonClasses.isEmpty()) {
			Map<PokemonClass, List<String>> classMap =
					PokemonBackendModule.instance().getConfig().pokemonClasses;
			for (PokemonClass pc : tc.pokemonClasses) {
				List<String> classSpecies = classMap.get(pc);
				if (classSpecies != null) {
					union.addAll(classSpecies);
				}
			}
		}
		if (tc.extraSpecies != null) {
			union.addAll(tc.extraSpecies);
		}
		union.removeAll(excluded);
		if (tc.bstRange != null) {
			int min = (int) tc.bstRange.getMin();
			int max = (int) tc.bstRange.getMax();
			union.removeIf(name -> {
				com.cobblemon.mod.common.pokemon.Species sp = PokemonSpecies.INSTANCE.getByName(name);
				if (sp == null) return true;
				int bst = sp.getBaseStats().values().stream().mapToInt(Integer::intValue).sum();
				return bst < min || bst > max;
			});
		}
		return new ArrayList<>(union);
	}

	private int resolveLevel(@NotNull TierConfig tc, @NotNull BossSpawnEvent ev) {
		if (ev.getLevel() != null) {
			int lvl = ev.getLevel();
			if (lvl < tc.levelRange.getMin() || lvl > tc.levelRange.getMax()) {
				return -1; // reject; no clamp
			}
			return lvl;
		}
		return (int) RandomUtils.getRandom(tc.levelRange);
	}

	/** Build + register the boss. Returns Pokémon UUID on success; null on failure (partial state rolled back). */
	private @Nullable UUID spawnBoss(
			@NotNull MinecraftServer server,
			@NotNull ServerPlayer anchor,
			@NotNull BlockPos position,
			@NotNull BossTier tier,
			@NotNull TierConfig tc,
			@NotNull SpeciesPick pick,
			int level,
			boolean shiny,
			boolean systemSpawned
	) {
		ServerLevel sl = anchor.serverLevel();
		String species = pick.species();

		Pokemon pokemon = null;
		PokemonEntity entity = null;
		UUID pokemonUUID = null;
		boolean entityAdded = false;
		boolean teamApplied = false;
		boolean registered = false;
		// success → true only after every post-register step completes; otherwise finally unwinds.
		boolean success = false;

		try {
			PokemonProperties props = new PokemonProperties();
			props.setSpecies(species);
			props.setLevel(level);
			props.setShiny(shiny);
			if (pick.megaAspect() != null) {
				props.setAspects(Set.of(pick.megaAspect()));
			}
			if (tc.maxIvs) {
				// minPerfectIVs = 6 → all 6 stats guaranteed at IVs.MAX_VALUE (31).
				props.setIvs(IVs.createRandomIVs(6));
			}
			props.getCustomProperties().add(UncatchableProperty.INSTANCE.uncatchable());

			pokemon = new Pokemon();
			props.apply(pokemon);
			pokemon.initialize();
			// setForcedAspects sticks across initialize()'s aspect recompute (mirrors MegaEvolutionUtils).
			if (pick.megaAspect() != null) {
				pokemon.setForcedAspects(Set.of(pick.megaAspect()));
			}
			pokemon.teachLearnableMoves(true);
			if (tc.maxEvs) {
				applyMaxEvs(pokemon);
			}
			// Heal AFTER stat-affecting changes so currentHealth tracks final maxHealth (else battles start at <100%).
			pokemon.heal();
			pokemonUUID = pokemon.getUuid();

			entity = new PokemonEntity(sl, pokemon, com.cobblemon.mod.common.CobblemonEntities.POKEMON);
			entity.moveTo(position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0F, 0F);

			// Scale persists via Cobblemon's scaleModifier codec.
			new CobblemonPokemon(pokemon).setScale(tc.scale);

			// Boss markers on Cobblemon's Pokemon.persistentData. Captured by the entity's first save.
			long spawnedAt = System.currentTimeMillis();
			net.minecraft.nbt.CompoundTag tag = pokemon.getPersistentData();
			tag.putBoolean(BossNbtKeys.BOSS, true);
			tag.putInt(BossNbtKeys.SCHEMA, BossNbtKeys.SCHEMA_VERSION);
			tag.putString(BossNbtKeys.TIER, tier.name());
			tag.putString(BossNbtKeys.SPECIES, species);
			tag.putInt(BossNbtKeys.LEVEL, level);
			tag.putLong(BossNbtKeys.SPAWNED_AT, spawnedAt);
			tag.putBoolean(BossNbtKeys.SYSTEM_SPAWNED, systemSpawned);
			tag.putInt(BossNbtKeys.SPAWN_X, position.getX());
			tag.putInt(BossNbtKeys.SPAWN_Y, position.getY());
			tag.putInt(BossNbtKeys.SPAWN_Z, position.getZ());
			tag.putString(BossNbtKeys.SPAWN_DIMENSION, sl.dimension().location().toString());

			applyBossName(entity, tc, species, level);

			// Set BEFORE addFreshEntity so Cobblemon's despawner can't grab the entity first tick.
			// setPersistenceRequired: blocks checkDespawn + survives chunk unload.
			// setInvulnerable: blocks direct-damage cheese; battles bypass entity.hurt() so unaffected.
			entity.setPersistenceRequired();
			entity.setInvulnerable(true);

			if (!sl.addFreshEntity(entity)) {
				Logger.warn("Boss spawn rejected by world (addFreshEntity returned false) for " + species
						+ " at " + position);
				return null;
			}
			entityAdded = true;

			// Defer 500ms so clients track the entity before the team-membership packet (else glow drops). Mirrors handleEntityLoad.
			final PokemonEntity entityRef = entity;
			com.raduvoinea.utils.lambda.ScheduleUtils.runTaskLater(
					() -> BossFabricModule.instance().runOnMain(() -> {
						if (!entityRef.isRemoved()) {
							manager.applyBossTeam(server, entityRef, tc);
						}
					}),
					com.raduvoinea.utils.generic.Time.milliseconds(500)
			);
			teamApplied = true;

			if (tc.spawnEffect != null && tc.spawnEffect.enabled) {
				double cy = entity.getY() + entity.getBbHeight() / 2.0;
				for (var p : tc.spawnEffect.particleOptions) {
					sl.sendParticles(p, entity.getX(), cy, entity.getZ(),
							tc.spawnEffect.count,
							tc.spawnEffect.offset, tc.spawnEffect.offset, tc.spawnEffect.offset, 0.0);
				}
			}

			// Counter already reserved upstream (tryReserveTier for system, no-op for admin).
			ActiveBoss boss = new ActiveBoss(
					pokemonUUID, entity.getUUID(), tier, species, level, spawnedAt,
					systemSpawned, position, sl.dimension().location()
			);
			if (!manager.registerActive(boss)) {
				Logger.warn("Boss already registered for UUID " + pokemonUUID + "; rolling back");
				return null;
			}
			registered = true;

			manager.scheduleDespawn(boss);
			manager.scheduleAmbientParticles(boss);
			announceSpawn(tier, tc, species, level, entity);
			success = true;
			Logger.info("Boss spawned: " + tier + " " + species + " lv." + level
					+ " (" + BossManager.shortId(pokemonUUID) + ")"
					+ (shiny ? " ✨" : "")
					+ " at (" + position.getX() + "," + position.getY() + "," + position.getZ() + ")"
					+ (systemSpawned ? " [system]" : " [admin]"));
			return pokemonUUID;
		} catch (Throwable t) {
			Logger.error("Boss spawn pipeline failed for " + species + " at " + position
					+ " — " + StackTraceUtils.toString(t));
			return null;
		} finally {
			if (!success) {
				rollbackPartialSpawn(server, pokemon, entity, pokemonUUID,
						entityAdded, teamApplied, registered);
			}
		}
	}

	/** Undo partial spawn side effects. Caller releases the tier counter separately. */
	private void rollbackPartialSpawn(
			@NotNull MinecraftServer server,
			@Nullable Pokemon pokemon,
			@Nullable PokemonEntity entity,
			@Nullable UUID pokemonUUID,
			boolean entityAdded,
			boolean teamApplied,
			boolean registered
	) {
		if (registered && pokemonUUID != null) {
			manager.rollbackRegistration(pokemonUUID);
		}
		if (teamApplied && pokemonUUID != null) {
			manager.removeBossTeam(server, pokemonUUID);
		}
		if (entityAdded && entity != null) {
			if (pokemon != null) {
				pokemon.getPersistentData().putBoolean(BossNbtKeys.BOSS, false);
			}
			entity.discard();
		}
	}

	public void applyBossName(PokemonEntity entity, TierConfig tc, String species, int level) {
		// Per-tier nameplate override wins; else fall back to the global lang template.
		com.raduvoinea.utils.message_builder.MessageBuilder base = (tc.nameplateFormat != null)
				? new com.raduvoinea.utils.message_builder.MessageBuilder(tc.nameplateFormat)
				: config.lang.bossDisplayName;

		String formatted = base
				.parse("glow_color", tc.glowColor.toLowerCase())
				.parse("display_name", tc.displayName)
				.parse("tier_display", tc.displayName)
				.parse("species", species)
				.parse("level", level)
				.parse();

		Component nameComp = BossFabricModule.instance().getMiniMessageManager().parse(formatted);
		// PokemonEntity.setCustomName flattens to an unstyled literal (Component.literal(getString())) —
		// set the nickname directly so gradients/colors survive into the nameplate and battle GUI.
		entity.getPokemon().setNickname(nameComp.copy());
		entity.setCustomNameVisible(true);
	}

	/** Balanced 510-EV spread — 85 to each of the 6 stats. */
	private void applyMaxEvs(@NotNull Pokemon pokemon) {
		com.cobblemon.mod.common.pokemon.EVs evs = pokemon.getEvs();
		evs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.HP, 85);
		evs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK, 85);
		evs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE, 85);
		evs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK, 85);
		evs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE, 85);
		evs.set(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED, 85);
	}

	private void announceSpawn(@NotNull BossTier tier, @NotNull TierConfig tc, @NotNull String species,
	                           int level, @NotNull PokemonEntity entity) {
		gg.mmorealms.module.boss.common.AnnounceLevel lvl = tc.announceOnSpawn;
		if (lvl == null || lvl == gg.mmorealms.module.boss.common.AnnounceLevel.OFF) {
			return;
		}
		int x = (int) entity.getX();
		int y = (int) entity.getY();
		int z = (int) entity.getZ();
		String glow = tc.glowColor.toLowerCase();

		switch (lvl) {
			case WORLD_CHAT -> {
				String biome = readableBiome((net.minecraft.server.level.ServerLevel) entity.level(), entity.blockPosition());
				String message = config.lang.bossSpawnedAnnouncementWorld
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", species)
						.parse("level", level)
						.parse("biome", biome)
						.parse("x", x).parse("y", y).parse("z", z)
						.parse();
				net.minecraft.network.chat.Component comp = BossFabricModule.instance()
						.getMiniMessageManager().parse(message);
				for (ServerPlayer p : ((net.minecraft.server.level.ServerLevel) entity.level()).players()) {
					p.sendSystemMessage(comp);
				}
			}
			case GLOBAL_CHAT -> {
				String message = config.lang.bossSpawnedAnnouncementGlobal
						.parse("glow_color", glow)
						.parse("tier_display", tc.displayName)
						.parse("species", species)
						.parse("level", level)
						.parse();
				new gg.mmorealms.module.chat.common.dto.GlobalMessageEvent(message).send();
			}
			default -> {}
		}
	}

	/** Player-readable biome name — mirrors {@code LegendaryInfoUtils.createSpawnInfo}. */
	private static String readableBiome(@NotNull net.minecraft.server.level.ServerLevel level, @NotNull BlockPos pos) {
		try {
			net.minecraft.resources.ResourceLocation id = net.minecraft.resources.ResourceLocation
					.parse(level.getBiome(pos).getRegisteredName());
			return net.minecraft.network.chat.Component.translatable(id.toLanguageKey("biome")).getString();
		} catch (Throwable t) {
			return "the Wild";
		}
	}
}
