package gg.mmorealms.module.core.backend.fabric.utils;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.mmorealms.module.core.backend.common.utils.ResourceUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;


public class PolymerItemUtils {

    public static PolymerModelData itemModel(String path) {
        return itemModel(path, Items.PAPER);
    }

    public static PolymerModelData itemModel(String path, Item virtualItem) {
        return PolymerResourcePackUtils.requestModel(virtualItem, ResourceUtils.modResource("item/" + path));
    }

    public static PolymerModelData blockModel(String path) {
        return blockModel(path, Items.STONE);
    }

    public static PolymerModelData blockModel(String path, Item virtualItem) {
        return PolymerResourcePackUtils.requestModel(virtualItem, ResourceUtils.modResource("block/" + path));
    }

}
