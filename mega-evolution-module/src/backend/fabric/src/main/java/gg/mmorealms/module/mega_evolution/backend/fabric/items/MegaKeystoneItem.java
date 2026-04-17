package gg.mmorealms.module.mega_evolution.backend.fabric.items;

import gg.mmorealms.module.core.backend.fabric.items.PolymerNamedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class MegaKeystoneItem extends PolymerNamedItem {

    public MegaKeystoneItem(String modelPath) {
        super("Keystone", modelPath, Items.DIAMOND, new Item.Properties().stacksTo(1));
    }

}
