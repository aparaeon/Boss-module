package gg.mmorealms.module.mega_evolution.backend.fabric.datagen;

import gg.mmorealms.module.mega_evolution.backend.fabric.world.MegaConfiguredFeatures;
import gg.mmorealms.module.mega_evolution.backend.fabric.world.MegaPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class MegaEvolutionDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(MegaEvolutionBlockLootTableProvider::new);
        pack.addProvider(MegaEvolutionModelProvider::new);
        pack.addProvider(MegaRegistryDataGenerator::new);
        pack.addProvider(MegaEvolutionBlockTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, MegaConfiguredFeatures::bootstrap);
        registryBuilder.add(Registries.PLACED_FEATURE, MegaPlacedFeatures::bootstrap);
    }
}
