package gg.mmorealms.module.boss.backend.fabric.manager;

import gg.mmorealms.module.boss.backend.fabric.config.BossConfig;
import gg.mmorealms.module.boss.backend.fabric.config.TierConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// TODO(v2): extract to core-module/SpawnPositionFinder.
// Light-weight finder for wild-boss MVP. Legendaries has a heavier finder
// (LegendarySpawner.SpawnPositionFinder ~250 lines with cave/underwater spawning +
// DimensionSpawnConfiguration). We use a simpler donut-around-player approach which
// suffices for surface wild spawns.
//
// MVP supports:
//   - Donut spawn radius around anchor (spawnRangeFromPlayer.min .. .max)
//   - Heightmap-based surface Y (ignoreLeaves toggle)
//   - Per-tier biome whitelist (fully-qualified IDs only — #tag entries are v2)
//   - Per-tier or global allowedDimensions filter (anchor's dimension only — no cross-dim teleport)
//   - canPokemonFitCheckRadius footprint clearance (matters for large-scale bosses up to 3.0x)
//   - Fluid rejection at footprint + below center (no underwater/lava spawns in v1)
//
// Explicitly v2 (NOT implemented):
//   - Cave / underground spawning
//   - Underwater / lava spawning
//   - Biome tag (#tag) lookup
//   - Nether/End custom heightmap awareness (dimension whitelist mitigates)
public final class SpawnPositionFinder {

	private SpawnPositionFinder() {}

	public static @Nullable BlockPos find(
			@NotNull ServerPlayer anchor,
			@NotNull TierConfig tier,
			@NotNull BossConfig global
	) {
		ServerLevel level = anchor.serverLevel();

		// Dimension filter
		List<String> allowedDims = tier.allowedDimensions != null && !tier.allowedDimensions.isEmpty()
				? tier.allowedDimensions
				: global.allowedDimensions;
		String currentDim = level.dimension().location().toString();
		if (!allowedDims.contains(currentDim)) {
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

			// Biome whitelist (if non-empty). Match repo convention: getRegisteredName() per
			// legendaries-module/.../LegendaryInfoUtils.java:21. Fully-qualified IDs only in MVP.
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

		return null;
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
				// TODO(v2): biome tag support via BiomeTags lookup.
				continue;
			}
			if (entry.equalsIgnoreCase(biomeId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Footprint clearance check over a (2r+1)x(2r+1) square centered on pos.
	 * For each (x, z) cell:
	 *   - pos (y) and pos+1 (y+1) must be air AND fluid-free (no underwater/lava in v1)
	 * Center additionally requires:
	 *   - pos-1 (y-1) is solid AND fluid-free (avoid lava-on-ground or floating water)
	 * radius 0 → only center column (1x1 footprint, vanilla pokemon size).
	 * radius 1 → 3x3 footprint (MVP default — balances "looks reasonable for scaled bosses" vs
	 *            "doesn't starve in trees/mountains/villages"). Admin can raise to 2 (5x5) for
	 *            true 3.0x-scale clearance if they accept higher failure rate.
	 */
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
