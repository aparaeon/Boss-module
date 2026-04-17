package gg.mmorealms.module.core.backend.fabric.blocks;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import gg.mmorealms.module.core.backend.common.utils.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public abstract class PolymerBlockWithSound extends Block implements PolymerTexturedBlock {

    public PolymerBlockWithSound(Properties properties) {
        super(properties);
    }

    @Override
    public void destroy(@NotNull LevelAccessor levelAccessor,
                        @NotNull BlockPos blockPos,
                        @NotNull BlockState blockState) {
        SoundUtils.playBreakSound(levelAccessor, blockPos, blockState);
    }

    @Override
    protected void onPlace(@NotNull BlockState state,
                           @NotNull Level level,
                           @NotNull BlockPos blockPos,
                           @NotNull BlockState oldState,
                           boolean movedByPiston) {
        SoundUtils.playPlaceSound(level, blockPos, state);
    }

}
