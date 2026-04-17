package gg.mmorealms.module.core.backend.fabric.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public abstract class PolymerDeepslateBlock extends PolymerBlockWithSound {

    public PolymerDeepslateBlock() {
        this(Properties.of());
    }

    public PolymerDeepslateBlock(Properties properties) {
        super(properties
                .strength(4.5f, 3f)
                .mapColor(MapColor.DEEPSLATE)
                .sound(SoundType.DEEPSLATE)
                .requiresCorrectToolForDrops());
    }

}
