package gg.mmorealms.module.boss.backend.fabric.manager;

import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// Donut-around-player surface finder. Supports: dimension filter, biome whitelist (FQ IDs + #tags),
// footprint clearance, fluid rejection. No cave/underwater support yet.
public final class SpawnPositionFinder {

	private SpawnPositionFinder() {}

	public static @Nullable BlockPos find(
			@NotNull ServerPlayer anchor,
			@NotNull TierConfig tier,
			@NotNull BossConfig global
	) {
		ServerLevel level = anchor.serverLevel();

		// Dimension filter
		String currentDim = level.dimension().location().toString();
		if (!allowedDimensions(tier, global).contains(currentDim)) {
			return null;
		}

		Heightmap.Types heightmapType = global.ignoreLeaves
				? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
				: Heightmap.Types.MOTION_BLOCKING;

		ThreadLocalRandom rng = ThreadLocalRandom.current();
		int minRadius = global.spawnRangeFromPlayer.getMin();
		int maxRadius = global.spawnRangeFromPlayer.getMax();
		BlockPos anchorPos = anchor.blockPosition();
		int fitRadius = Math.max(0, global.canPokemonFitCheckRadius);

		for (int attempt = 0; attempt < global.maxCandidateAttempts; attempt++) {
			// Pick a random offset in the donut [minRadius, maxRadius].
			int radius = rng.nextInt(minRadius, maxRadius + 1);
			double angle = rng.nextDouble() * Math.PI * 2.0;
			int dx = (int) Math.round(Math.cos(angle) * radius);
			int dz = (int) Math.round(Math.sin(angle) * radius);
			int x = anchorPos.getX() + dx;
			int z = anchorPos.getZ() + dz;

			// Heightmap surface lookup.
			int y = level.getHeight(heightmapType, x, z);
			if (y < global.randomSpawnRangeY.getMin() || y > global.randomSpawnRangeY.getMax()) {
				continue;
			}

			BlockPos pos = new BlockPos(x, y, z);

			// Biome whitelist match — supports fully-qualified IDs and #tags.
			if (tier.biomes != null && !tier.biomes.isEmpty()) {
				if (!biomeMatches(level, pos, tier.biomes)) {
					continue;
				}
			}

			// Footprint clearance over canPokemonFitCheckRadius square.
			if (!hasFootprintClearance(level, pos, fitRadius)) {
				continue;
			}

			return pos;
		}

		// No anchor-column fallback — it bypassed the biome/Y checks and could drop a boss on the
		// anchor's head. Callers retry later (refill sweep) instead.
		return null;
	}

	/** Tier override wins; else the global list. Also used by {@link BossManager} to pre-filter refill anchors. */
	public static @NotNull List<String> allowedDimensions(@NotNull TierConfig tier, @NotNull BossConfig global) {
		return tier.allowedDimensions != null && !tier.allowedDimensions.isEmpty()
				? tier.allowedDimensions
				: global.allowedDimensions;
	}

	private static boolean biomeMatches(ServerLevel level, BlockPos pos, List<String> whitelist) {
		String biomeId;
		try {
			biomeId = level.getBiome(pos).getRegisteredName();
		} catch (Throwable t) {
			return false;
		}
		if (biomeId == null) {
			return false;
		}
		for (String entry : whitelist) {
			if (entry.startsWith("#")) {
				// Biome tag — resolve and check holder membership (mirrors legendaries' BiomeManager.processBiomeTag).
				try {
					TagKey<Biome> tagKey = TagKey.create(Registries.BIOME,
							ResourceLocation.parse(entry.substring(1)));
					if (level.getBiome(pos).is(tagKey)) {
						return true;
					}
				} catch (Throwable t) {
					// Malformed tag string — treat as no match, same as the old silent-skip behavior.
				}
				continue;
			}
			if (entry.equalsIgnoreCase(biomeId)) {
				return true;
			}
		}
		return false;
	}

	/** Footprint clearance over (2r+1)² cells: head+feet air, center floor solid; all fluid-free. */
	private static boolean hasFootprintClearance(ServerLevel level, BlockPos center, int radius) {
		// Center column: must have solid, fluid-free ground.
		BlockPos below = center.below();
		if (level.getBlockState(below).isAir()) {
			return false;
		}
		if (!level.getFluidState(below).isEmpty()) {
			return false;
		}

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				BlockPos floor = center.offset(dx, 0, dz);
				BlockPos head = floor.above();
				if (!level.getBlockState(floor).isAir() || !level.getBlockState(head).isAir()) {
					return false;
				}
				if (!level.getFluidState(floor).isEmpty() || !level.getFluidState(head).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
}
