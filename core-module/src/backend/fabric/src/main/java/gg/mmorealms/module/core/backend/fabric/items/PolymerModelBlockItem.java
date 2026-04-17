package gg.mmorealms.module.core.backend.fabric.items;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import gg.mmorealms.module.core.backend.fabric.utils.PolymerItemUtils;
import lombok.Getter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;


@Getter
public class PolymerModelBlockItem extends BlockItem implements IPolymerModelItem {
    private final PolymerModelData polymerModelData;

    public PolymerModelBlockItem(String modelPath, Block block) {
        this(modelPath, DEFAULT_BLOCK_ITEM, block);
    }

    public PolymerModelBlockItem(String modelPath, Item virtualItem, Block block) {
        this(modelPath, virtualItem, block, new Properties());
    }

    public PolymerModelBlockItem(String modelPath, Item virtualItem, Block block, Properties properties) {
        super(block, properties);
        this.polymerModelData = PolymerItemUtils.blockModel(modelPath, virtualItem);
    }

    public PolymerModelBlockItem(PolymerModelData polymerModelData, Block block) {
        this(polymerModelData, block, new Properties());
    }

    public PolymerModelBlockItem(PolymerModelData polymerModelData, Block block, Properties properties) {
        super(block, properties);
        this.polymerModelData = polymerModelData;
    }
}
