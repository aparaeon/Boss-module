package gg.mmorealms.module.mega_evolution.backend.fabric.registry;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerRegistryUtils;
import gg.mmorealms.module.mega_evolution.backend.fabric.dto.MegaEvolution;
import gg.mmorealms.module.mega_evolution.backend.fabric.items.MegaBraceletItem;
import gg.mmorealms.module.mega_evolution.backend.fabric.items.MegaEvolutionGemItem;
import gg.mmorealms.module.mega_evolution.backend.fabric.items.MegaKeystoneItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class MegaEvolutionItems {

    private MegaEvolutionItems() {
    }

    public static Map<MegaEvolution, Item> MEGA_GEMS = MegaEvolution.linkedMapWithGems(MegaEvolutionItems::registerMegaGem);
    public static Map<MegaEvolution, BlockItem> MEGA_STONE_ORES = MegaEvolution.linkedMapWithGems(MegaEvolutionItems::registerMegaStoneOreBlockItem);
    public static Map<MegaEvolution, BlockItem> MEGA_DEEPSLATE_ORES = MegaEvolution.linkedMapWithGems(MegaEvolutionItems::registerMegaDeepslateOreBlockItem);

    public static Item MEGA_BRACELET = PolymerRegistryUtils.registerItem(MegaEvolutionDataKeys.MEGA_BRACELET_KEY, new MegaBraceletItem(MegaEvolutionDataKeys.MEGA_BRACELET_KEY));
    public static Item KEYSTONE = PolymerRegistryUtils.registerItem(MegaEvolutionDataKeys.KEYSTONE_KEY, new MegaKeystoneItem(MegaEvolutionDataKeys.KEYSTONE_KEY));

    public static final CreativeModeTab ITEM_GROUP = CreativeModeTab.builder(null, -1)
            .title(Component.literal("Mega Evolution Items"))
            .icon(MEGA_BRACELET::getDefaultInstance)
            .displayItems((ctx, output) -> {
                output.accept(MEGA_BRACELET);
                output.accept(KEYSTONE);
                MEGA_GEMS.values().forEach(output::accept);
            })
            .build();

    public static final CreativeModeTab BLOCK_ITEM_GROUP = CreativeModeTab.builder(null, -1)
            .title(Component.literal("Mega Evolution Blocks"))
            .icon(MEGA_STONE_ORES.get(MegaEvolution.MAWILE)::getDefaultInstance)
            .displayItems((ctx, output) -> {
                MEGA_STONE_ORES.values().forEach(output::accept);
                MEGA_DEEPSLATE_ORES.values().forEach(output::accept);
            })
            .build();

    public static void register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceUtils.modResource("mega_evolution_items"), ITEM_GROUP);
        PolymerItemGroupUtils.registerPolymerItemGroup(ResourceUtils.modResource("mega_evolution_blocks"), BLOCK_ITEM_GROUP);
    }

    private static Item registerMegaGem(MegaEvolution evolution) {
        String gemName = evolution.getGemName();
        return PolymerRegistryUtils.registerItem(gemName, new MegaEvolutionGemItem(evolution));
    }

    private static BlockItem registerMegaStoneOreBlockItem(MegaEvolution evolution) {
        Block block = MegaEvolutionBlocks.MEGA_STONE_ORES.get(evolution);
        return PolymerRegistryUtils.registerNamedBlockItem(evolution.getStoneOreDisplayName(), evolution.getStoneOreName(), Items.STONE, block);
    }

    private static BlockItem registerMegaDeepslateOreBlockItem(MegaEvolution evolution) {
        Block block = MegaEvolutionBlocks.MEGA_DEEPSLATE_ORES.get(evolution);
        return PolymerRegistryUtils.registerNamedBlockItem(evolution.getDeepslateOreDisplayName(), evolution.getDeepslateOreName(), Items.DEEPSLATE, block);
    }

}
