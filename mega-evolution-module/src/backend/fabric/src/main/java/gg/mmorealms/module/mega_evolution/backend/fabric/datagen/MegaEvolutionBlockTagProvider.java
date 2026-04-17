package gg.mmorealms.module.mega_evolution.backend.fabric.datagen;

import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class MegaEvolutionBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public MegaEvolutionBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        FabricTagBuilder pickaxeMineable = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
        FabricTagBuilder diamondToolRequired = getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL);

        MegaEvolution.streamWithGems().forEach(evolution -> {
            Block stoneOre = MegaEvolutionBlocks.MEGA_STONE_ORES.get(evolution);
            Block deepslateOre = MegaEvolutionBlocks.MEGA_DEEPSLATE_ORES.get(evolution);

            ResourceKey<Block> stoneOreKey = BuiltInRegistries.BLOCK.getResourceKey(stoneOre).orElseThrow();
            ResourceKey<Block> deepslateOreKey = BuiltInRegistries.BLOCK.getResourceKey(deepslateOre).orElseThrow();

            pickaxeMineable.add(stoneOreKey);
            pickaxeMineable.add(deepslateOreKey);

            diamondToolRequired.add(stoneOreKey);
            diamondToolRequired.add(deepslateOreKey);
        });
    }
}
