package gg.mmorealms.module.mega_evolution.backend.fabric.world.gen;

import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolutionGem;
import gg.mmorealms.module.mega_evolution.backend.fabric.world.MegaPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.world.level.levelgen.GenerationStep;

public class MegaOreGeneration {

    private MegaOreGeneration() { }

    public static void generate() {
        MegaEvolution.streamWithGems()
                .forEach(evolution -> {
                    MegaEvolutionGem gem = evolution.getGem();

                    if (gem == null) {
                        throw new RuntimeException("MegaEvolutionGem must not be null");
                    }

                    BiomeModifications.addFeature(
                            gem.getBiomeSelector(),
                            GenerationStep.Decoration.UNDERGROUND_ORES,
                            MegaPlacedFeatures.getPlacedFeatureKey(evolution));
                });
    }

}
