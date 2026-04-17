package gg.mmorealms.module.core.backend.fabric.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public interface IPolymerModelItem extends PolymerItem {
    Item DEFAULT_ITEM = Items.PAPER;
    Item DEFAULT_BLOCK_ITEM = Items.STONE;

    PolymerModelData getPolymerModelData();

    @Override
    default Item getPolymerItem(ItemStack stack, @Nullable ServerPlayer player) {
        return getPolymerModelData().item();
    }

    @Override
    default int getPolymerCustomModelData(ItemStack stack, @Nullable ServerPlayer player) {
        return getPolymerModelData().value();
    }
}
