package gg.mmorealms.module.boss.backend.fabric.manager;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.ActiveBoss;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import gg.mmorealms.module.boss.common.BossTier;
import gg.mmorealms.module.boss.common.BossTierTheme;
import gg.mmorealms.module.boss.common.event.BossSpawnEvent;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class.PokemonClass;
import gg.mmorealms.module.pokemon.backend.fabric.dto.pokemon_implementation.CobblemonPokemon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BossSpawner {

	private static final @Nullable EntityDataAccessor<Component> NICKNAME_ACCESSOR = resolveNicknameAccessor();

	private final BossConfig config;
	private final BossManager manager;

	@SuppressWarnings("unchecked")
	private static @Nullable EntityDataAccessor<Component> resolveNicknameAccessor() {
		try {
			java.lang.reflect.Field field = PokemonEntity.class.getDeclaredField("NICKNAME");
			field.setAccessible(true);
			return (EntityDataAccessor<Component>) field.get(null);
		} catch (ReflectiveOperationException | ClassCastException exception) {
			Logger.error("Could not resolve PokemonEntity.NICKNAME; boss nameplates will not colour on fresh spawns: "
					+ exception.getMessage());
			return null;
		}
	}

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
		} catch (RuntimeException t) {
			Logger.error("Admin boss spawn pipeline failed for tier " + tier
					+ " — " + StackTraceUtils.toString(t));
			return AdminSpawnResult.of(AdminSpawnStatus.SPAWN_FAILED);
		}
	}
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
		} catch (RuntimeException t) {
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

		List<ServerPlayer> online = ev.getEligiblePlayerUUIDs().stream()
				.map(server.getPlayerList()::getPlayer)
				.filter(Objects::nonNull)
				.toList();

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
		BossTier spawnTier = resolveUncappedTier(ev.getTier());
		if (spawnTier == null) {
			Logger.debug("BossScheduler: all tiers at cap; tick dropped.");
			return;
		}
		TierConfig spawnTc = config.tiers.get(spawnTier);

		boolean owned = false;
		try {
			SpeciesPick pick = resolveSpecies(spawnTier, spawnTc, ev.getSpecies(), /*enforceTierPool=*/ true);
			if (pick == null) {
				Logger.warn("Could not resolve species for tier " + spawnTier + "; aborting");
				return;
			}

			int level = resolveLevel(spawnTc, ev);
			if (level < 0) {
				return;
			}

			BlockPos position = SpawnPositionFinder.find(anchor, spawnTc, config);
			if (position == null) {
				Logger.warn("Could not find valid spawn position for " + pick.species()
						+ " near " + anchor.getName().getString());
				return;
			}

			owned = spawnBoss(server, anchor, position, spawnTier, spawnTc, pick, level, ev.isShiny(), /*systemSpawned=*/ true) != null;
		} catch (RuntimeException t) {
			Logger.error("Boss spawn failed for tier " + spawnTier
					+ " — " + StackTraceUtils.toString(t));
		} finally {
			if (!owned) {
				manager.releaseTier(spawnTier);
			}
		}
	}

	private @Nullable BossTier resolveUncappedTier(@NotNull BossTier requested) {
		if (manager.tryReserveTier(requested)) {
			return requested;
		}
		List<BossTier> others = new ArrayList<>(List.of(BossTier.values()));
		others.remove(requested);
		Collections.shuffle(others);
		for (BossTier candidate : others) {
			if (config.tiers.containsKey(candidate) && manager.tryReserveTier(candidate)) {
				Logger.info("Tier " + requested + " at cap; re-rolled to " + candidate);
				return candidate;
			}
		}
		return null;
	}

	private record SpeciesPick(String species, @Nullable String megaAspect) {}

	private static @Nullable String normalizeSpecies(@Nullable String species) {
		if (species == null) return null;
		String normalized = species.trim().toLowerCase(Locale.ROOT);
		return normalized.isEmpty() ? null : normalized;
	}

	private @Nullable SpeciesPick resolveSpecies(@NotNull BossTier tier, @NotNull TierConfig tc,
	                                             @Nullable String forcedInput, boolean enforceTierPool) {
		String forced = normalizeSpecies(forcedInput);
		if (forced != null) {
			if (PokemonSpecies.INSTANCE.getByName(forced) == null) {
				return null;
			}
			if (enforceTierPool && !speciesInTierPool(tier, tc, forced)) {
				return null;
			}
			String forcedAspect = tier == BossTier.MEGA ? megaAspect(forced) : null;
			if (tier == BossTier.MEGA && forcedAspect == null && enforceTierPool) return null;
			return new SpeciesPick(forced, forcedAspect);
		}

		if (tier == BossTier.MEGA) {
			List<MegaEvolution> candidates = megaCandidates(tc);
			if (candidates.isEmpty()) {
				return null;
			}
			MegaEvolution chosen = candidates.get(RandomUtils.getRandom(0, candidates.size() - 1));
			return new SpeciesPick(chosen.getSpeciesName(), chosen.getMegaAspect());
		}

		List<String> pool = buildSpeciesPool(tc);
		if (pool.isEmpty()) {
			return null;
		}
		String picked = pool.get(RandomUtils.getRandom(0, pool.size() - 1));
		return new SpeciesPick(picked, null);
	}

	private @NotNull List<MegaEvolution> megaCandidates(@NotNull TierConfig tc) {
		return MegaEvolution.stream()
				.filter(me -> me.getSpeciesName() != null
						&& PokemonSpecies.INSTANCE.getByName(normalizeSpecies(me.getSpeciesName())) != null)
				.filter(me -> me.getMegaAspect() != null && !me.getMegaAspect().isEmpty())
				.filter(me -> tc.includeAllMegaCapable
						|| tc.extraSpecies.stream().anyMatch(extra -> extra.equalsIgnoreCase(me.getSpeciesName())))
				.collect(Collectors.toList());
	}

	private static @Nullable String megaAspect(@NotNull String species) {
		return MegaEvolution.stream()
				.filter(me -> me.getSpeciesName() != null && me.getSpeciesName().equalsIgnoreCase(species))
				.findFirst()
				.map(MegaEvolution::getMegaAspect)
				.orElse(null);
	}

	private boolean speciesInTierPool(@NotNull BossTier tier, @NotNull TierConfig tc, @NotNull String species) {
		if (tier == BossTier.MEGA) {
			return megaCandidates(tc).stream()
					.anyMatch(me -> me.getSpeciesName().equalsIgnoreCase(species));
		}
		return buildSpeciesPool(tc).stream().anyMatch(s -> s.equalsIgnoreCase(species));
	}

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

		if (tc.pokemonClasses != null && !tc.pokemonClasses.isEmpty()) {
			Map<PokemonClass, List<String>> classMap = PokemonBackendModule.instance().getConfig().pokemonClasses;
			tc.pokemonClasses.stream().map(classMap::get).filter(Objects::nonNull).forEach(union::addAll);
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
				return -1;
			}
			return lvl;
		}
		return (int) RandomUtils.getRandom(tc.levelRange);
	}

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
				props.setIvs(IVs.createRandomIVs(6));
			}
			props.getCustomProperties().add(UncatchableProperty.INSTANCE.uncatchable());
			com.cobblemon.mod.common.pokemon.Species speciesObj = PokemonSpecies.INSTANCE.getByName(species);
			List<String> bestMoves = BossMovesetPlanner.planStatic(tier, speciesObj, level);
			if (!bestMoves.isEmpty()) {
				props.setMoves(bestMoves);
				Logger.debug("Boss moveset " + tier + " " + species + " lv." + level + ": " + String.join(", ", bestMoves));
			}
			String nature = pickNature(speciesObj, level);
			if (nature != null) {
				props.setNature(nature);
			}

			pokemon = new Pokemon();
			props.apply(pokemon);
			pokemon.initialize();
			if (pick.megaAspect() != null) {
				pokemon.setForcedAspects(Set.of(pick.megaAspect()));
			}
			pokemon.teachLearnableMoves(false);
			if (pick.megaAspect() == null) {
				applyBossAbility(pokemon, tc);
			}
			if (tc.maxEvs) {
				applyFocusedEvs(pokemon, speciesObj, level);
			}
			applyHeldItem(pokemon, tc);
			pokemon.heal();
			pokemonUUID = pokemon.getUuid();

			entity = new PokemonEntity(sl, pokemon, com.cobblemon.mod.common.CobblemonEntities.POKEMON);
			entity.moveTo(position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0F, 0F);

			new CobblemonPokemon(pokemon).setScale(tc.scale);

			long spawnedAt = System.currentTimeMillis();
			net.minecraft.nbt.CompoundTag tag = pokemon.getPersistentData();
			tag.putBoolean(NbtKeys.BOSS, true);
			tag.putInt(NbtKeys.SCHEMA, NbtKeys.SCHEMA_VERSION);
			tag.putString(NbtKeys.TIER, tier.name());
			tag.putString(NbtKeys.SPECIES, species);
			tag.putInt(NbtKeys.LEVEL, level);
			tag.putLong(NbtKeys.SPAWNED_AT, spawnedAt);
			tag.putBoolean(NbtKeys.SYSTEM_SPAWNED, systemSpawned);
			tag.putInt(NbtKeys.SPAWN_X, position.getX());
			tag.putInt(NbtKeys.SPAWN_Y, position.getY());
			tag.putInt(NbtKeys.SPAWN_Z, position.getZ());
			tag.putString(NbtKeys.SPAWN_DIMENSION, sl.dimension().location().toString());

			applyBossName(entity, tier, species, level);

			entity.setPersistenceRequired();
			entity.setInvulnerable(true);

			if (!sl.addFreshEntity(entity)) {
				Logger.warn("Boss spawn rejected by world (addFreshEntity returned false) for " + species
						+ " at " + position);
				return null;
			}
			entityAdded = true;

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
		} catch (RuntimeException t) {
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
				pokemon.getPersistentData().putBoolean(NbtKeys.BOSS, false);
			}
			entity.discard();
		}
	}

	public void applyBossName(@NotNull PokemonEntity entity, @NotNull BossTier tier, @NotNull String species, int level) {
		String formatted = config.lang.bossDisplayName
				.parse("tier_start", BossTierTheme.tierLineStart(tier))
				.parse("tier_end", BossTierTheme.tierLineEnd(tier))
				.parse("tier_display", BossTierTheme.tierPlainName(tier))
				.parse("species", BossManager.speciesDisplayName(species))
				.parse("level", level)
				.parse();

		Component nameComp = BossFabricModule.instance().getMiniMessageManager().parse(formatted);
		Component label = Component.literal(" ").append(nameComp).append(" ");
		entity.getPokemon().setNickname(label.copy());
		if (NICKNAME_ACCESSOR != null) {
			entity.getEntityData().set(NICKNAME_ACCESSOR, label.copy());
		}
		entity.setCustomNameVisible(true);
	}

	private void applyFocusedEvs(@NotNull Pokemon pokemon, @Nullable com.cobblemon.mod.common.pokemon.Species species, int level) {
		com.cobblemon.mod.common.pokemon.EVs evs = pokemon.getEvs();
		double[] power = offensePower(species, level);
		boolean physical = power[0] >= power[1];
		evs.set(Stats.HP, 252);
		evs.set(physical ? Stats.ATTACK : Stats.SPECIAL_ATTACK, 252);
		evs.set(Stats.SPEED, 4);
	}

	private void applyBossAbility(@NotNull Pokemon pokemon, @NotNull TierConfig tc) {
		if (tc.bossAbility == null || tc.bossAbility.isBlank()) {
			return;
		}
		com.cobblemon.mod.common.api.abilities.AbilityTemplate template =
				com.cobblemon.mod.common.api.abilities.Abilities.get(tc.bossAbility);
		if (template == null) {
			Logger.warn("Boss ability '" + tc.bossAbility + "' did not resolve to a known ability; skipping.");
			return;
		}
		pokemon.updateAbility(template.create(true, com.cobblemon.mod.common.api.Priority.NORMAL));
	}

	private void applyHeldItem(@NotNull Pokemon pokemon, @NotNull TierConfig tc) {
		if (tc.heldItem == null || tc.heldItem.isBlank()) {
			return;
		}
		net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM
				.get(net.minecraft.resources.ResourceLocation.parse(tc.heldItem));
		if (item == net.minecraft.world.item.Items.AIR) {
			Logger.warn("Boss heldItem '" + tc.heldItem + "' did not resolve to an item; skipping.");
			return;
		}
		pokemon.swapHeldItem(new net.minecraft.world.item.ItemStack(item), false, false);
	}

	private static double[] offensePower(@Nullable com.cobblemon.mod.common.pokemon.Species species, int level) {
		if (species == null) {
			return new double[]{0, 0};
		}
		com.cobblemon.mod.common.api.pokemon.moves.Learnset learnset = species.getMoves();
		Set<com.cobblemon.mod.common.api.moves.MoveTemplate> pool = new HashSet<>();
		pool.addAll(learnset.getLevelUpMovesUpTo(level));
		pool.addAll(learnset.getTmMoves());
		double physicalPower = 0;
		double specialPower = 0;
		for (com.cobblemon.mod.common.api.moves.MoveTemplate move : pool) {
			if (move.getPower() <= 0) {
				continue;
			}
			String category = move.getDamageCategory().getName();
			if ("physical".equalsIgnoreCase(category)) {
				physicalPower += move.getPower();
			} else if ("special".equalsIgnoreCase(category)) {
				specialPower += move.getPower();
			}
		}
		return new double[]{physicalPower, specialPower};
	}

	private void announceSpawn(@NotNull BossTier tier, @NotNull TierConfig tc, @NotNull String species,
	                           int level, @NotNull PokemonEntity entity) {
		TierConfig.AnnounceLevel lvl = tc.announceOnSpawn;
		if (lvl == null || lvl == TierConfig.AnnounceLevel.OFF) {
			return;
		}
		int x = (int) entity.getX();
		int y = (int) entity.getY();
		int z = (int) entity.getZ();
		String glow = tc.glowColor.toLowerCase();
		String speciesDisplay = BossManager.speciesDisplayName(species);

		switch (lvl) {
			case WORLD_CHAT -> {
				String biome = readableBiome((net.minecraft.server.level.ServerLevel) entity.level(), entity.blockPosition());
				String message = config.lang.bossSpawnedAnnouncementWorld
						.parse("announcement_title", BossTierTheme.title(tier, BossTierTheme.BannerKind.SPAWN))
						.parse("announcement_line", BossTierTheme.spawnLine(tier, speciesDisplay, biome))
						.parse("tier_display", tc.displayName)
						.parse("species", speciesDisplay)
						.parse("species_display", speciesDisplay)
						.parse("level", level)
						.parse("biome", biome)
						.parse("x", x).parse("y", y).parse("z", z)
						.parse();
				BossManager.sendWorldChat((net.minecraft.server.level.ServerLevel) entity.level(), message);
			}
			case GLOBAL_CHAT -> {
				String biome = readableBiome((net.minecraft.server.level.ServerLevel) entity.level(), entity.blockPosition());
				String message = config.lang.bossSpawnedAnnouncementGlobal
						.parse("announcement_title", BossTierTheme.title(tier, BossTierTheme.BannerKind.SPAWN))
						.parse("announcement_line", BossTierTheme.spawnLine(tier, speciesDisplay, biome))
						.parse();
				BossManager.sendGlobalChat(message);
			}
			default -> {}
		}
	}

	private static @Nullable String pickNature(@Nullable com.cobblemon.mod.common.pokemon.Species species, int level) {
		if (species == null) return null;
		double[] power = offensePower(species, level);
		double physicalPower = power[0];
		double specialPower = power[1];

		if (physicalPower > specialPower * 1.2) {
			return com.cobblemon.mod.common.api.pokemon.Natures.ADAMANT.getName().toString();
		}
		if (specialPower > physicalPower * 1.2) {
			return com.cobblemon.mod.common.api.pokemon.Natures.MODEST.getName().toString();
		}
		return null;
	}


	private static String readableBiome(@NotNull net.minecraft.server.level.ServerLevel level, @NotNull BlockPos pos) {
		try {
			net.minecraft.resources.ResourceLocation id = net.minecraft.resources.ResourceLocation
					.parse(level.getBiome(pos).getRegisteredName());
			return net.minecraft.network.chat.Component.translatable(id.toLanguageKey("biome")).getString();
		} catch (RuntimeException t) {
			return "the Wild";
		}
	}
}
