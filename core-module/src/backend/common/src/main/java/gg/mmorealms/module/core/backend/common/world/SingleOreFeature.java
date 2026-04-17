package gg.mmorealms.module.core.backend.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class SingleOreFeature extends Feature<SingleOreConfiguration> {

    public SingleOreFeature(Codec<SingleOreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SingleOreConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        SingleOreConfiguration config = context.config();
        RandomSource random = context.random();

        if (config.targetStates.isEmpty()) {
            return false;
        }

        int searchRadius = config.searchRadius;
        int maxAttempts = searchRadius * searchRadius * 2;
        int span = searchRadius * 2 + 1;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            BlockPos targetPos = origin.offset(
                    random.nextInt(span) - searchRadius,
                    random.nextInt(span) - searchRadius,
                    random.nextInt(span) - searchRadius
            );

            if (tryPlaceOre(level, targetPos, config, random)) {
                return true;
            }
        }

        return false;
    }

    private boolean tryPlaceOre(WorldGenLevel level, BlockPos pos, SingleOreConfiguration config, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        for (OreConfiguration.TargetBlockState target : config.targetStates) {
            if (!target.target.test(currentState, random)) {
                continue;
            }

            if (config.discardChanceOnAirExposure > 0.0f
                    && hasAirExposure(level, pos)
                    && random.nextFloat() < config.discardChanceOnAirExposure) {
                return false; // Skip this position
            }

            level.setBlock(pos, target.state, 2);
            return true;
        }

        return false;
    }

    private boolean hasAirExposure(WorldGenLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).isAir()) {
                return true;
            }
        }

        return false;
    }

}
