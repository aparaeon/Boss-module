package gg.mmorealms.loader.backend.common.mixin;

import gg.mmorealms.loader.backend.common.dto.event.fabric.CropStompEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(FarmBlock.class)
public class FarmBlockMixin {
	@Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
	private void onPlayerJumpedOnCrop(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
		CropStompEvent event = new CropStompEvent(level, entity);
		Boolean result = event.fireSync();

		if (result != null && !result) {
			ci.cancel();
		}
	}
}