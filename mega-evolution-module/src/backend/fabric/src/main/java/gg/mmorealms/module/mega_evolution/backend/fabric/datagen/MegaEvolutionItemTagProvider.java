package gg.mmorealms.module.mega_evolution.backend.fabric.datagen;

import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionItems;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class MegaEvolutionItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public MegaEvolutionItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        FabricTagBuilder megaStoneTag =
                getOrCreateTagBuilder(MegaEvolutionTags.MEGA_GEM);

        MegaEvolution.streamWithGems().forEach(evolution -> {
            Item gem = MegaEvolutionItems.MEGA_GEMS.get(evolution);
            ResourceKey<Item> gemKey = BuiltInRegistries.ITEM.getResourceKey(gem).orElseThrow();
            megaStoneTag.add(gemKey);
        });
    }

}
