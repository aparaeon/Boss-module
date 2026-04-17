package gg.mmorealms.module.mega_evolution.backend.fabric.datagen;

import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionBlocks;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.concurrent.CompletableFuture;

public class MegaEvolutionBlockLootTableProvider extends FabricBlockLootTableProvider {

    protected MegaEvolutionBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        MegaEvolution.streamWithGems().forEach(evolution -> {
            Block stoneOreBlock = MegaEvolutionBlocks.MEGA_STONE_ORES.get(evolution);
            Block deepsalteOreBlock = MegaEvolutionBlocks.MEGA_DEEPSLATE_ORES.get(evolution);
            Item gemItem = MegaEvolutionItems.MEGA_GEMS.get(evolution);

            add(stoneOreBlock,
                    createSilkTouchDispatchTable(stoneOreBlock,
                            applyExplosionDecay(stoneOreBlock,
                                    LootItem.lootTableItem(gemItem)
                            )
                    )
            );

            add(deepsalteOreBlock,
                    createSilkTouchDispatchTable(deepsalteOreBlock,
                            applyExplosionDecay(deepsalteOreBlock,
                                    LootItem.lootTableItem(gemItem)
                            )
                    )
            );
        });
    }

}
