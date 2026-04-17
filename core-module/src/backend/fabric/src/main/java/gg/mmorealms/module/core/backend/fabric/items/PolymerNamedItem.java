package gg.mmorealms.module.core.backend.fabric.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PolymerNamedItem extends PolymerModelItem {
    private final Component name;

    public PolymerNamedItem(String name, String modelPath) {
        this(name, modelPath, DEFAULT_ITEM, new Properties());
    }

    public PolymerNamedItem(String name, String modelPath, Properties properties) {
        this(name, modelPath, DEFAULT_ITEM, properties);
    }

    public PolymerNamedItem(String name, String modelPath, Item virtualItem) {
        this(name, modelPath, virtualItem, new Properties());
    }

    public PolymerNamedItem(String name, String modelPath, Item virtualItem, Properties properties) {
        this(Component.literal(name), modelPath, virtualItem, properties);
    }

    public PolymerNamedItem(Component name, String modelPath, Item virtualItem, Properties properties) {
        super(modelPath, virtualItem, properties);
        this.name = name;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return name;
    }
}
