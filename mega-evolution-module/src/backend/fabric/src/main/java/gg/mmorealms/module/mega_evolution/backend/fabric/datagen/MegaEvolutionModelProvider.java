package gg.mmorealms.module.mega_evolution.backend.fabric.datagen;

import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionBlocks;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MegaEvolutionModelProvider extends FabricModelProvider {

    public MegaEvolutionModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        MegaEvolution.streamWithGems().forEach(evolution -> {
            Block stoneOreBlock = MegaEvolutionBlocks.MEGA_STONE_ORES.get(evolution);
            generators.createTrivialCube(stoneOreBlock);

            Block deepslateOreBlock = MegaEvolutionBlocks.MEGA_DEEPSLATE_ORES.get(evolution);
            generators.createTrivialCube(deepslateOreBlock);
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(MegaEvolutionItems.KEYSTONE, ModelTemplates.FLAT_ITEM);

        MegaEvolution.streamWithGems().forEach(evolution -> {
            Item gemItem = MegaEvolutionItems.MEGA_GEMS.get(evolution);
            generators.generateFlatItem(gemItem, ModelTemplates.FLAT_ITEM);
        });
    }

}
