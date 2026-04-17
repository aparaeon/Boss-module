package gg.mmorealms.module.core.backend.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.List;

public class SingleOreConfiguration extends OreConfiguration {
    public static final Codec<SingleOreConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter(config -> config.targetStates),
                    Codec.intRange(1, 10).fieldOf("search_radius").forGetter(config -> config.searchRadius),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter(config -> config.discardChanceOnAirExposure)
            ).apply(instance, SingleOreConfiguration::new)
    );

    public final int searchRadius;

    public SingleOreConfiguration(List<TargetBlockState> targets, int searchRadius, float discardChance) {
        super(targets, 1, discardChance); // Always size 1 for single ore
        this.searchRadius = searchRadius;
    }

    public SingleOreConfiguration(List<TargetBlockState> targets, int searchRadius) {
        this(targets, searchRadius, 0.0f);
    }

    public SingleOreConfiguration(List<TargetBlockState> targets) {
        this(targets, 1, 0.0f);
    }
}
