package gg.mmorealms.module.core.backend.fabric.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public abstract class PolymerStoneBlock extends PolymerBlockWithSound {

    public PolymerStoneBlock() {
        this(Properties.of());
    }

    public PolymerStoneBlock(Properties properties) {
        super(properties
                .strength(3f, 3f)
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }

}
