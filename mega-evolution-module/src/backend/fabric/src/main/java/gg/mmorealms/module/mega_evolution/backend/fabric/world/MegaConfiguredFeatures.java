package gg.mmorealms.module.mega_evolution.backend.fabric.world;

import gg.mmorealms.module.core.backend.common.world.ModFeatures;
import gg.mmorealms.module.core.backend.common.world.SingleOreConfiguration;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionBlocks;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MegaConfiguredFeatures {

    private MegaConfiguredFeatures() { }

    // Store the registered keys for use in placed features
    private static final Map<MegaEvolution, ResourceKey<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_KEYS = new HashMap<>();

    public static void initializeKeys() {
        MegaEvolution.streamWithGems()
                .forEach(evolution -> {
                    String oreName = evolution.getStoneOreName();
                    ResourceKey<ConfiguredFeature<?, ?>> key = PolymerRegistryUtils.registerConfigureFeatureKey(oreName);
                    CONFIGURED_FEATURE_KEYS.put(evolution, key);
                });
    }

    // For datagen
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        MegaEvolution.streamWithGems()
                .forEach(evolution -> registerMegaOre(context, evolution));
    }

    private static void registerMegaOre(BootstrapContext<ConfiguredFeature<?, ?>> context, MegaEvolution evolution) {
        ResourceKey<ConfiguredFeature<?, ?>> key = CONFIGURED_FEATURE_KEYS.get(evolution);

        SingleOreConfiguration config = createOreConfiguration(evolution);
        context.register(key, new ConfiguredFeature<>(ModFeatures.SINGLE_ORE, config));
    }

    private static SingleOreConfiguration createOreConfiguration(MegaEvolution evolution) {
        BlockState stoneOreBlockState = MegaEvolutionBlocks.MEGA_STONE_ORES.get(evolution).defaultBlockState();
        BlockState deepslateOreBlockState = MegaEvolutionBlocks.MEGA_DEEPSLATE_ORES.get(evolution).defaultBlockState();

        List<SingleOreConfiguration.TargetBlockState> targetBlocks = List.of(
                SingleOreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), stoneOreBlockState),
                SingleOreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), deepslateOreBlockState)
        );

        return new SingleOreConfiguration(targetBlocks);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeatureKey(MegaEvolution megaEvolution) {
        return CONFIGURED_FEATURE_KEYS.get(megaEvolution);
    }
}