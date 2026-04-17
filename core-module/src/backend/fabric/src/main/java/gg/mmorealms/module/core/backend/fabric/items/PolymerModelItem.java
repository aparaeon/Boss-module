package gg.mmorealms.module.core.backend.fabric.items;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerItemUtils;
import lombok.Getter;
import net.minecraft.world.item.Item;

@Getter
public class PolymerModelItem extends Item implements IPolymerModelItem {
    private PolymerModelData polymerModelData;

    public PolymerModelItem(String modelPath) {
        this(modelPath, DEFAULT_ITEM, new Properties());
    }

    public PolymerModelItem(String modelPath, Properties properties) {
        this(modelPath, DEFAULT_ITEM, properties);
    }

    public PolymerModelItem(String modelPath, Item virtualItem) {
        this(modelPath, virtualItem, new Properties());
    }

    public PolymerModelItem(String modelPath, Item virtualItem, Properties properties) {
        this(properties);
        this.polymerModelData = PolymerItemUtils.itemModel(modelPath, virtualItem);
    }

    public PolymerModelItem(PolymerModelData polymerModelData, Properties properties) {
        this(properties);
        this.polymerModelData = polymerModelData;
    }

    public PolymerModelItem(Properties properties) {
        super(properties);
    }
}
