package gg.mmorealms.module.mega_evolution.backend.fabric.world;

import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolutionGem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MegaPlacedFeatures {

    private MegaPlacedFeatures() { }

    private static final Map<MegaEvolution, ResourceKey<PlacedFeature>> PLACED_FEATURE_KEYS = new HashMap<>();

    public static void initializeKeys() {
        MegaEvolution.streamWithGems()
                .forEach(evolution -> {
                    String oreName = evolution.getStoneOreName();
                    ResourceKey<PlacedFeature> key = PolymerRegistryUtils.registerPlacedFeatureKey(oreName);
                    PLACED_FEATURE_KEYS.put(evolution, key);
                });
    }

    // For datagen
    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        MegaEvolution.streamWithGems()
                .forEach(evolution -> registerMegaOrePlacement(context, configuredFeatures, evolution));
    }

    private static void registerMegaOrePlacement(BootstrapContext<PlacedFeature> context,
                                                 HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
                                                 MegaEvolution evolution) {

        ResourceKey<PlacedFeature> key = PLACED_FEATURE_KEYS.get(evolution);

        ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey =
                MegaConfiguredFeatures.getConfiguredFeatureKey(evolution);

        Holder<ConfiguredFeature<?, ?>> configuredFeature =
                configuredFeatures.getOrThrow(configuredFeatureKey);

        List<PlacementModifier> placementModifiers = createOrePlacementModifiers(evolution);

        context.register(key, new PlacedFeature(configuredFeature, placementModifiers));
    }

    private static List<PlacementModifier> createOrePlacementModifiers(MegaEvolution evolution) {
        MegaEvolutionGem gem = evolution.getGem();

        if (gem == null) {
            throw new RuntimeException("Gem must not be null");
        }

        return PolymerRegistryUtils.rareOrePlacement(gem.getVeinEveryChunk(),
                HeightRangePlacement.triangle(
                        VerticalAnchor.absolute(gem.getMinHeight()),
                        VerticalAnchor.absolute(gem.getMaxHeight())
                )
        );
    }

    public static ResourceKey<PlacedFeature> getPlacedFeatureKey(MegaEvolution megaEvolution) {
        return PLACED_FEATURE_KEYS.get(megaEvolution);
    }

}