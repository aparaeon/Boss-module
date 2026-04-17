package gg.mmorealms.module.core.backend.fabric.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class PolymerNamedBlockItem extends PolymerModelBlockItem {
    private final Component name;

    public PolymerNamedBlockItem(String name, String modelPath, Block block) {
        this(name, modelPath, DEFAULT_BLOCK_ITEM, block, new Properties());
    }

    public PolymerNamedBlockItem(String name, String modelPath, Block block, Properties properties) {
        this(name, modelPath, DEFAULT_BLOCK_ITEM, block, properties);
    }

    public PolymerNamedBlockItem(String name, String modelPath, Item virtualItem, Block block) {
        this(name, modelPath, virtualItem, block, new Properties());
    }

    public PolymerNamedBlockItem(String name, String modelPath, Item virtualItem, Block block, Properties properties) {
        this(Component.literal(name), modelPath, virtualItem, block, properties);
    }

    public PolymerNamedBlockItem(Component name, String modelPath, Item virtualItem, Block block, Properties properties) {
        super(modelPath, virtualItem, block, properties);
        this.name = name;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return name;
    }
}
