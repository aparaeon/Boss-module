package gg.mmorealms.module.legendaries.backend.fabric.manager;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.datafixers.util.Pair;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.core.common.utils.AliasTable;
import gg.mmorealms.module.legendaries.backend.fabric.LegendariesModule;
import gg.mmorealms.module.legendaries.backend.fabric.config.DimensionSpawnConfiguration;
import gg.mmorealms.module.legendaries.backend.fabric.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.backend.fabric.dto.ICaptureLockable;
import gg.mmorealms.module.legendaries.backend.fabric.dto.LegendarySpawnData;
import gg.mmorealms.module.legendaries.backend.fabric.dto.enums.TimeOfDay;
import gg.mmorealms.module.legendaries.backend.fabric.dto.enums.Weather;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Handles spawn logic for legendaries
 */
@SuppressWarnings("resource")
public class LegendarySpawner {
    private final LegendarySpawnConfig config;
    private final MinecraftServer server;

    private final BiomeManager biomeManager;
    private final SpawnPositionFinder positionFinder;
    private final PokemonFactory pokemonFactory;

    /* ---------- Records ---------- */

    private record TimeWeather(TimeOfDay time, Weather weather) {
    }

    private record PokemonSpatialData(
            String species,
            boolean underground,
            boolean underwater,
            Map<ResourceKey<Level>, Set<Holder<Biome>>> dimensionBiomes
    ) {
    }

    public record PokemonSpawnData(
            String species,
            BlockPos blockPos,
            ServerLevel level,
            Set<UUID> allowedCatchers
    ) {
    }

    /* ---------- Caches ---------- */

    private final Map<TimeWeather, AliasTable<PokemonSpatialData>> spawnCandidates = new HashMap<>();

    // Build the spawn candidate tables for all time+weather combinations
    private void buildSpawnCandidateTables() {
        // Process each legendary Pokémon
        config.legendaries.values().forEach(this::addPokemonToRelevantTables);
        // Build all alias tables for efficient sampling
        spawnCandidates.values().forEach(AliasTable::build);
    }

    private void addPokemonToRelevantTables(LegendarySpawnData.PokemonData legendary) {
        LegendarySpawnData.Conditions conditions = legendary.getConditions();

        // Build dimension -> biome mapping for this Pokémon
        Map<ResourceKey<Level>, Set<Holder<Biome>>> dimensions =
                biomeManager.buildDimensionBiomeMapping(legendary);
        if (dimensions.isEmpty()) return;

        // Create spatial data object
        PokemonSpatialData spatialData = new PokemonSpatialData(
                legendary.getSpec(),
                conditions.isUnderground(),
                conditions.isUnderwater(),
                dimensions
        );

        // Add to each relevant time+weather table
        for (var t : conditions.expandTimes())
            for (var w : conditions.expandWeathers()) {
                TimeWeather tw = new TimeWeather(t, w);
                spawnCandidates.computeIfAbsent(tw, k -> new AliasTable<>())
                        .add(spatialData, legendary.getWeight());
            }
    }

    /* ---------- Core ---------- */

    public LegendarySpawner() {
        this.config = LegendariesModule.instance().getConfig();
        this.server = LegendariesModule.instance().getServer();

        this.biomeManager = new BiomeManager(config, this.server);
        this.positionFinder = new SpawnPositionFinder(config);
        this.pokemonFactory = new PokemonFactory(config);

        buildSpawnCandidateTables();
    }

    public CompletableFuture<PokemonEntity> attemptLegendarySpawnAsync() {

        return CompletableFuture
                .supplyAsync(this::prepareSpawn) // Preparation phase (off-thread)
                .thenApplyAsync(spawnData -> { // Spawn phase (on-thread)
                    if (spawnData == null) {
                        return null;
                    }
                    return finalizeSpawn(spawnData);
                }, server);
    }

    @Nullable
    public PokemonSpawnData prepareSpawn() {
        // Early exit if no active players on server with spawnOnPlayersAbsent condition false
        if (server.getPlayerList().getPlayerCount() == 0 && !config.spawnOnPlayersAbsent) {
            Logger.warn("There is no players in wild to spawn legendary around.");
            return null;
        }
        // Get current environment conditions
        ServerLevel overworld = server.overworld();
        TimeWeather currentConditions = getCurrentTimeWeather(overworld);
        if (currentConditions == null) {
            return null;
        }

        // Find eligible Pokémon for current conditions
        AliasTable<PokemonSpatialData> candidates = spawnCandidates.get(currentConditions);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        // Try multiple candidates if needed
        for (int i = 0; i < config.maxCandidateAttempts; i++) {
            PokemonSpatialData pokemonData = candidates.next();
            Logger.debug("Pokemon spawn candidate: " + pokemonData);
            PokemonSpawnData spawnData = prepareCandidate(pokemonData);
            if (spawnData != null) {
                return spawnData;
            }
        }

        return null;
    }

    @Nullable
    public PokemonEntity finalizeSpawn(PokemonSpawnData spawnData) {
        return pokemonFactory.createAndSpawn(
                spawnData.species,
                spawnData.blockPos,
                spawnData.level,
                spawnData.allowedCatchers
        );
    }

    @Nullable
    private TimeWeather getCurrentTimeWeather(ServerLevel level) {
        List<TimeOfDay> times = TimeOfDay.getAllMatchingTimes(level.dayTime(), true);
        if (times.isEmpty()) return null;

        TimeOfDay time = RandomUtils.getRandom(times);
        Weather weather = Weather.fromWorldState(level);
        return new TimeWeather(time, weather);
    }

    @Nullable
    private PokemonSpawnData prepareCandidate(PokemonSpatialData pokemonData) {
        if (pokemonData == null) {
            return null;
        }

        // Select random dimension from eligible ones
        ResourceKey<Level> dimension = chooseRandomDimension(pokemonData);
        if (dimension == null) {
            return null;
        }

        // Get ServerLevel of dimension
        ServerLevel level = server.getLevel(dimension);
        if (level == null) {
            return null;
        }

        // Get random player for this level (or null if spawning without players)
        ServerPlayer selectedPlayer = getRandomPlayerForLevel(level);

        // Find spawn location
        BlockPos spawnPos = findSpawnPosition(level, pokemonData, selectedPlayer);
        if (spawnPos == null) {
            return null;
        }

        // Create allowed catchers set
        Set<UUID> allowedCatchers = selectedPlayer != null ?
                getAllowedCatchers(selectedPlayer) :
                Collections.emptySet();

        return new PokemonSpawnData(
                pokemonData.species,
                spawnPos,
                level,
                allowedCatchers
        );
    }

    @Nullable
    private ResourceKey<Level> chooseRandomDimension(PokemonSpatialData data) {
        List<ResourceKey<Level>> eligibleDimensions = new ArrayList<>(data.dimensionBiomes().keySet());
        return eligibleDimensions.isEmpty() ?
                null :
                RandomUtils.getRandom(eligibleDimensions);
    }

    @Nullable
    private BlockPos findSpawnPosition(ServerLevel level, PokemonSpatialData data, @Nullable ServerPlayer selectedPlayer) {
        // Generate random starting position
        BlockPos startPos;
        Range biomeRange;

        if (selectedPlayer != null) {
            startPos = getPlayerPositionXZ(selectedPlayer);
            biomeRange = config.spawnRange;
        } else if (config.spawnOnPlayersAbsent) {
            startPos = getRandomPositionXZ(level);
            biomeRange = Range.of(0, config.biomeSearchRadius);
        } else {
            return null;
        }

        // Find valid biome
        Set<Holder<Biome>> eligibleBiomes = data.dimensionBiomes().get(level.dimension());
        Pair<BlockPos, Holder<Biome>> biomeResult =
                biomeManager.findEligibleBiome(startPos, biomeRange, eligibleBiomes, level);

        if (biomeResult == null) {
            return null;
        }

        PokemonEntity pokemonEntity = pokemonFactory.createPokemonEntity(data.species, level, Collections.emptySet());
        if (pokemonEntity == null) {
            return null;
        }

        // Try to find valid position with Pokémon collision check
        return positionFinder.findValidPosition(
                biomeResult.getFirst(),
                data.underground(),
                data.underwater(),
                biomeResult.getSecond(),
                level,
                pokemonEntity);
    }

    private Set<UUID> getAllowedCatchers(ServerPlayer player) {
        UUID catcher = player.getUUID();
        Set<UUID> allowed = new HashSet<>();
        allowed.add(catcher);

        IRealm realm = IRealm.getByOwner(player);
        if (realm == null) {
            return allowed;
        }

        if (!realm.isLegendaryCaptureShared()) {
            return allowed;
        }

        // Hibernate actually gives a Map<String, String> at runtime instead of Map<UUID, TrustLevel>,
        // so grab it as raw Objects with safe cast for case in the future it will return correct types
        @SuppressWarnings("unchecked")
        Map<UUID, TrustLevel> members =
                normalizeMembers((Map<Object, Object>) (Map<?, ?>) realm.getMembers());

        members.keySet().stream()
                .filter(uuid -> realm.checkPermission(uuid, RealmPermission.LEGENDARY_CAPTURE_SHARED))
                .forEach(allowed::add);

        Logger.debug("Allowed catchers: " + allowed);
        return allowed;
    }

    private Map<UUID, TrustLevel> normalizeMembers(Map<Object, Object> raw) {
        Map<UUID, TrustLevel> normalized = new HashMap<>();

        for (Map.Entry<Object, Object> entry : raw.entrySet()) {
            UUID key;
            TrustLevel value;

            Object rawKey = entry.getKey();
            if (rawKey instanceof UUID uuid) {
                key = uuid;
            } else {
                try {
                    key = UUID.fromString(rawKey.toString());
                } catch (IllegalArgumentException e) {
                    Logger.warn("Invalid UUID in realm members: " + rawKey);
                    continue;
                }
            }

            Object rawValue = entry.getValue();
            if (rawValue instanceof TrustLevel trustLevel) {
                value = trustLevel;
            } else {
                try {
                    value = TrustLevel.valueOf(rawValue.toString());
                } catch (IllegalArgumentException e) {
                    Logger.warn("Invalid TrustLevel in realm members: " + rawValue);
                    continue;
                }
            }

            normalized.put(key, value);
        }

        return normalized;
    }

    @Nullable
    private ServerPlayer getRandomPlayerForLevel(ServerLevel level) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers()
                .stream()
                .filter(player -> player.level() == level)
                .toList();

        return players.isEmpty() ?
                null :
                players.get(ThreadLocalRandom.current().nextInt(players.size()));
    }

    private BlockPos getPlayerPositionXZ(ServerPlayer player) {
        BlockPos basePos = player.getOnPos();
        return basePos.atY(config.biomeSearchStartPositionY);
    }

    private BlockPos getRandomPositionXZ(ServerLevel level) {
        return LocationUtils.getRandomBlockPos(
                level.dimension().location().getPath(),
                config.randomSpawnRangeX,
                Range.exact(config.biomeSearchStartPositionY),
                config.randomSpawnRangeZ
        );
    }
}

/**
 * Manages biome mappings and lookups
 */
class BiomeManager {
    private final LegendarySpawnConfig config;
    private final MinecraftServer server;

    // Dimension -> available minecraft biomes
    private final Map<ResourceKey<Level>, Set<Holder<Biome>>> dimensionBiomes = new HashMap<>();
    // Cobblemon tag -> minecraft biomes mapping
    private final Map<String, Set<Holder<Biome>>> cobblemonToMinecraftBiomes = new HashMap<>();

    public BiomeManager(LegendarySpawnConfig config, MinecraftServer server) {
        this.config = config;
        this.server = server;

        initialize();
    }

    void initialize() {
        cacheDimensionBiomes();
        cacheCobblemonTagBiomes();
    }

    private void cacheDimensionBiomes() {
        config.allowedDimensions.forEach(dimension -> {
            ResourceKey<Level> key = ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.parse(dimension)
            );
            ServerLevel level = server.getLevel(key);
            if (level != null) {
                dimensionBiomes.put(key,
                        level.getChunkSource()
                                .getGenerator()
                                .getBiomeSource()
                                .possibleBiomes()
                );
            }
        });
    }

    private void cacheCobblemonTagBiomes() {
        // Collect all unique biome tags from legendaries
        Set<String> allTags = config.legendaries.values().stream()
                .map(LegendarySpawnData.PokemonData::getBiomes)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        // Process each tag
        allTags.forEach(this::processBiomeTag);
    }

    private void processBiomeTag(String tag) {
        String plainTag = tag.startsWith("#") ? tag.substring(1) : tag;
        TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, ResourceLocation.parse(plainTag));

        // Find all biomes matching this tag across dimensions
        Set<Holder<Biome>> matchingBiomes = dimensionBiomes.values().stream()
                .flatMap(Collection::stream)
                .filter(holder -> holder.is(biomeTag))
                .collect(Collectors.toSet());

        cobblemonToMinecraftBiomes.put(tag, matchingBiomes);
    }

    Map<ResourceKey<Level>, Set<Holder<Biome>>> buildDimensionBiomeMapping(
            LegendarySpawnData.PokemonData legendary) {
        Map<ResourceKey<Level>, Set<Holder<Biome>>> result = new HashMap<>();
        List<String> biomeTags = legendary.getBiomes();

        if (biomeTags == null || biomeTags.isEmpty()) {
            return result;
        }

        // Process each dimension
        dimensionBiomes.forEach((dimension, dimBiomes) -> {
            Set<Holder<Biome>> matchingBiomes = biomeTags.stream()
                    .map(tag -> cobblemonToMinecraftBiomes.getOrDefault(tag, Collections.emptySet()))
                    .flatMap(Collection::stream)
                    .filter(dimBiomes::contains)
                    .collect(Collectors.toSet());

            if (!matchingBiomes.isEmpty()) {
                result.put(dimension, matchingBiomes);
            }
        });

        return result;
    }

    @Nullable
    Pair<BlockPos, Holder<Biome>> findEligibleBiome(
            BlockPos start,
            Range range,
            Set<Holder<Biome>> eligibleBiomes,
            ServerLevel level) {

        if (eligibleBiomes == null || eligibleBiomes.isEmpty()) {
            return null;
        }

        return findClosestBiome3dInRange(
                eligibleBiomes::contains,
                start,
                range,
                config.biomeSearchHorizontalStep,
                config.biomeSearchVerticalStep,
                level
        );
    }

    @Nullable
    public Pair<BlockPos, Holder<Biome>> findClosestBiome3dInRange(Predicate<Holder<Biome>> biomePredicate, BlockPos pos, Range range, int horizontalStep, int verticalStep, ServerLevel level) {
        BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();

        Set<Holder<Biome>> set = biomeSource.possibleBiomes()
                .stream().filter(biomePredicate).collect(Collectors.toUnmodifiableSet());

        if (set.isEmpty()) {
            return null;
        }

        int maxSpiralSteps = Math.floorDiv(range.getMax() - range.getMin(), horizontalStep);
        int[] heightLevels = Mth.outFromOrigin(pos.getY(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), verticalStep).toArray();

        for (BlockPos.MutableBlockPos mutableBlockPos : BlockPos.spiralAround(BlockPos.ZERO, maxSpiralSteps, Direction.EAST, Direction.SOUTH)) {
            if (mutableBlockPos.equals(BlockPos.ZERO)) {
                continue;
            }

            int rangeOffsetX = (int) Math.signum(mutableBlockPos.getX()) * range.getMin();
            int rangeOffsetZ = (int) Math.signum(mutableBlockPos.getZ()) * range.getMin();

            int stepOffsetX = mutableBlockPos.getX() * horizontalStep;
            int stepOffsetZ = mutableBlockPos.getZ() * horizontalStep;

            int j = pos.getX() + rangeOffsetX + stepOffsetX;
            int k = pos.getZ() + rangeOffsetZ + stepOffsetZ;

            int l = QuartPos.fromBlock(j);
            int m = QuartPos.fromBlock(k);

            for (int n : heightLevels) {
                int o = QuartPos.fromBlock(n);
                Holder<Biome> holder = biomeSource.getNoiseBiome(l, o, m, level.getChunkSource().randomState().sampler());
                if (set.contains(holder)) {
                    return Pair.of(new BlockPos(j, n, k), holder);
                }
            }
        }

        return null;
    }
}

/**
 * Handles finding valid spawn positions
 */
class SpawnPositionFinder {
    private final LegendarySpawnConfig config;

    public SpawnPositionFinder(LegendarySpawnConfig config) {
        this.config = config;
    }

    @Nullable
    BlockPos findValidPosition(BlockPos biomePos,
                               boolean underground,
                               boolean underwater,
                               Holder<Biome> biome,
                               ServerLevel level,
                               PokemonEntity pokemonEntity) {

        // Get the chunk from the biome position
        ChunkPos chunkPos = new ChunkPos(biomePos);
        // Load chunk up to LIGHT, to properly get sky access (underground)
        level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.LIGHT, true);

        List<BlockPos> columnPositions = new ArrayList<>();

        Heightmap.Types heightmapType = config.ignoreLeaves ?
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES :
                Heightmap.Types.MOTION_BLOCKING;

        int midX = chunkPos.getMiddleBlockX();
        int midZ = chunkPos.getMiddleBlockZ();
        // Add center position
        columnPositions.add(new BlockPos(
                midX,
                level.getHeight(heightmapType, midX, midZ),
                midZ
        ));
        // Add corners
        int minX = chunkPos.getMinBlockX();
        int maxX = chunkPos.getMaxBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int maxZ = chunkPos.getMaxBlockZ();
        for (int fx = 0; fx < 2; fx++) {
            for (int fz = 0; fz < 2; fz++) {
                int x = (fx == 0) ? minX : maxX;
                int z = (fz == 0) ? minZ : maxZ;
                int y = level.getHeight(heightmapType, x, z);
                columnPositions.add(new BlockPos(x, y, z));
            }
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // For each column position, try to find a valid Y
        for (BlockPos columnPos : columnPositions) {
            List<BlockPos> validPositionsInColumn = findValidPositionsInColumn(
                    columnPos, underground, underwater, biome, level);

            if (!validPositionsInColumn.isEmpty()) {
                // Shuffle for randomness
                Collections.shuffle(validPositionsInColumn, random);

                // Try each position to see if the Pokémon can fit
                for (BlockPos pos : validPositionsInColumn) {
                    BlockPos fitPos = findFitPositionInRadius(pokemonEntity, pos, level, config.canPokemonFitCheckRadius);
                    if (fitPos != null) {
                        return fitPos;
                    }
                }
            }
        }

        return null;
    }

    private List<BlockPos> findValidPositionsInColumn(BlockPos columnPos,
                                                      boolean underground,
                                                      boolean underwater,
                                                      Holder<Biome> biome,
                                                      ServerLevel level) {

        DimensionSpawnConfiguration levelConfig = getDimensionConfig(level.dimension());

        // Calculate height bounds
        int minY = Math.max(config.randomSpawnRangeY.getMin(), levelConfig.minSearchHeight());
        int maxY = Math.min(columnPos.getY(), Math.min(config.randomSpawnRangeY.getMax(), levelConfig.maxSearchHeight()));

        List<BlockPos> validPositions = new ArrayList<>();

        // Search from top down
        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = columnPos.atY(y);

            if (isValidPosition(pos, underground, underwater, biome, level)) {
                validPositions.add(getActualSpawnPos(pos, level));
            }
        }

        return validPositions;
    }

    @Nullable
    private BlockPos findFitPositionInRadius(PokemonEntity pokemon, BlockPos center, ServerLevel level, int maxRadius) {
        if (canPokemonFit(pokemon, center, level)) {
            return center;
        }

        // Iterate through positions in a spiral pattern
        for (BlockPos.MutableBlockPos offsetPos : BlockPos.spiralAround(BlockPos.ZERO, maxRadius, Direction.EAST, Direction.SOUTH)) {
            BlockPos pos = center.offset(offsetPos.getX(), 0, offsetPos.getZ());
            // Verify the block below is solid and the position is clear
            if (isSolidSurface(pos.below(), level)) {
                if (canPokemonFit(pokemon, pos, level)) {
                    return pos;
                }
            }
        }

        return null;
    }

    private DimensionSpawnConfiguration getDimensionConfig(ResourceKey<Level> dimension) {
        return config.dimensionConfigurations.getOrDefault(
                dimension.location().toString(),
                config.dimensionConfigurations.get(Level.OVERWORLD.location().toString())
        );
    }

    private boolean isValidPosition(BlockPos pos,
                                    boolean underground,
                                    boolean underwater,
                                    Holder<Biome> biome,
                                    ServerLevel level) {

        DimensionSpawnConfiguration cfg = getDimensionConfig(level.dimension());
        boolean checkSky = cfg.checkSkyAccess();

        boolean isUnderwater = level.isWaterAt(pos);
        boolean skyVisible;

        if (checkSky) {
            skyVisible = level.canSeeSky(pos.above());
        } else {
            // If the dimension does not care about sky, treat it as "visible" by default
            skyVisible = true;
        }
        boolean isUnderground = !skyVisible;

        if (underground != isUnderground) {
            return false;
        }
        if (underwater && isUnderwater) {
            return biome == level.getBiome(pos);
        } else if (isSolidSurface(pos, level)) {
            return biome == level.getBiome(pos);
        }

        return false;
    }

    private boolean isSolidSurface(BlockPos pos, ServerLevel level) {
        BlockState state = level.getBlockState(pos);
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        boolean isNonSolid = !state.isSolid()
                && !level.getFluidState(pos).is(FluidTags.LAVA);

        boolean isSolidGround = belowState.isSolid()
                && (!(state.getBlock() instanceof LeavesBlock) || !config.ignoreLeaves);

        return isNonSolid && isSolidGround;
    }

    private BlockPos getActualSpawnPos(BlockPos pos, ServerLevel level) {
        // For solid surfaces, we want to spawn above the block
        if (level.getBlockState(pos).isSolid()) {
            return pos.above();
        }
        return pos;
    }

    private boolean canPokemonFit(PokemonEntity pokemonEntity, BlockPos pos, ServerLevel level) {
        // Position the entity at the spawn position
        pokemonEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
        return level.noCollision(pokemonEntity);
    }

}

/**
 * Handles Pokémon creation and spawning
 */
class PokemonFactory {
    private final LegendarySpawnConfig config;

    public PokemonFactory(LegendarySpawnConfig config) {
        this.config = config;
    }

    @Nullable
    public PokemonEntity createAndSpawn(String spec, BlockPos pos, ServerLevel level, Set<UUID> allowedCatchers) {
        PokemonEntity entity = createPokemonEntity(spec, level, allowedCatchers);
        if (entity == null) {
            return null;
        }

        entity.setPersistenceRequired();
        entity.setPos(pos.getX(), pos.getY(), pos.getZ());

        return level.addFreshEntity(entity) ? entity : null;
    }

    @Nullable
    public PokemonEntity createPokemonEntity(String species, ServerLevel level, Set<UUID> allowedCatchers) {
        Pokemon pokemon = createPokemonFromSpec(species);
        if (pokemon == null) {
            Logger.warn("Failed to create Pokemon from spec: " + species);
            return null;
        }

        PokemonEntity pokemonEntity = new PokemonEntity(
                level,
                pokemon,
                CobblemonEntities.POKEMON
        );

        ((ICaptureLockable) pokemonEntity).core$setAllowedCatchers(allowedCatchers);

        return pokemonEntity;
    }

    @Nullable
    public Pokemon createPokemonFromSpec(String name) {
        String baseName = name;
        Set<String> aspectsToApply = new HashSet<>();

        // Extract aspects from name
        for (String aspect : config.aspects) {
            if (baseName.contains(aspect)) {
                baseName = baseName.replace(aspect, "").trim();
                aspectsToApply.add(aspect);
            }
        }

        // Create base Pokémon
        Species species = PokemonSpecies.INSTANCE.getByName(baseName);
        if (species == null) {
            return null;
        }

        // Create and configure the Pokémon
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);

        // Apply aspects if any were found
        if (!aspectsToApply.isEmpty()) {
            Set<String> newAspects = new HashSet<>(pokemon.getAspects());
            newAspects.addAll(aspectsToApply);
            pokemon.setForcedAspects(newAspects);
        }

        return pokemon;
    }
}