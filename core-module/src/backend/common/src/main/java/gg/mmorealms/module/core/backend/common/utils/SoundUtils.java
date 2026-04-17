package gg.mmorealms.module.core.backend.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class SoundUtils {

	private SoundUtils() { }

	public static void playPlaceSound(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
		SoundEvent breakSound = blockState.getSoundType().getPlaceSound();
		playSound(levelAccessor, blockPos, breakSound);
	}

	public static void playBreakSound(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
		SoundEvent breakSound = blockState.getSoundType().getBreakSound();
		playSound(levelAccessor, blockPos, breakSound);
	}

	public static void playSound(Player player, LevelAccessor levelAccessor, BlockPos blockPos, SoundEvent soundEvent) {
		playSound(player, levelAccessor, blockPos, soundEvent, SoundSource.BLOCKS);
	}

	public static void playSound(LevelAccessor levelAccessor, BlockPos blockPos, SoundEvent soundEvent) {
		playSound(null, levelAccessor, blockPos, soundEvent, SoundSource.BLOCKS);
	}

	public static void playSound(LevelAccessor levelAccessor, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource) {
		playSound(null, levelAccessor, blockPos, soundEvent, soundSource);
	}

	public static void playSound(Player player, LevelAccessor levelAccessor, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource) {
		levelAccessor.playSound(player, blockPos, soundEvent, soundSource, 0.7f, 0.9f + levelAccessor.getRandom().nextFloat() * 0.2f);
	}

}
