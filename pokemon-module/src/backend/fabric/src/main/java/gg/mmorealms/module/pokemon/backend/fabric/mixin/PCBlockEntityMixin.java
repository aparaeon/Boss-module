package gg.mmorealms.module.pokemon.backend.fabric.mixin;

import com.cobblemon.mod.common.block.PCBlock;
import com.cobblemon.mod.common.block.entity.PCBlockEntity;
import com.raduvoinea.utils.logger.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PCBlockEntity.class)
public abstract class PCBlockEntityMixin {

	@Inject(method = "togglePCOn", at = @At("HEAD"), cancellable = true, remap = false)
	private void togglePCOn(boolean on, CallbackInfo ci) {
		PCBlockEntity blockEntity = (PCBlockEntity) (Object) this;
		BlockState blockState = (blockEntity).getBlockState();

		PCBlock pcBlock = (PCBlock) blockState.getBlock();

		Level world = blockEntity.getLevel();
		if (!(world != null && !world.isClientSide)) {
			return;
		}

		BlockPos posBottom = pcBlock.getBasePosition(blockState, blockEntity.getBlockPos());
		BlockState stateBottom = world.getBlockState(posBottom);

		if (!stateBottom.hasProperty(PCBlock.Companion.getPART())) {
			Logger.error("PC block bottom part property not found, saved by mixin");
			ci.cancel();
		}

		BlockPos posTop = pcBlock.getPositionOfOtherPart(stateBottom, posBottom);
		BlockState stateTop = world.getBlockState(posTop);

		if (!stateTop.hasProperty(PCBlock.Companion.getPART())) {
			Logger.error("PC block top part property not found, saved by mixin");
			ci.cancel();
		}
	}
}
