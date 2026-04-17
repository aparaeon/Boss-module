package gg.mmorealms.module.gyms.backend.fabric.mixin.bag;

import com.cobblemon.mod.common.item.interactive.ReviveItem;
import gg.mmorealms.module.gyms.backend.fabric.helper.MixinCommon;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReviveItem.class)
public class ReviveItemMixin {

	@Inject(method = "use",
			at = @At("HEAD"),
			cancellable = true)
	public void onInvoke(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (!MixinCommon.bagClauseVerification(user)) {
			cir.setReturnValue(InteractionResultHolder.fail(user.getItemInHand(hand)));
			cir.cancel();
		}
	}

}