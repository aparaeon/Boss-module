package gg.mmorealms.loader.backend.common.mixin;

import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerPlaceMinecartEvent;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartItem.class)
public class MinecartItemMixin {

	@Inject(method = "useOn",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/world/item/context/UseOnContext;getItemInHand()Lnet/minecraft/world/item/ItemStack;"
		), cancellable = true)
	private void onCreate(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
		if ("true".equalsIgnoreCase(SecretsUtils.getEnvironmentVariable("DISABLE_MINECART_CRASH_FIX"))) {
			return;
		}

		boolean result = new PlayerPlaceMinecartEvent(context).fireSync();
		if (!result) {
			cir.setReturnValue(InteractionResult.FAIL);
			cir.cancel();
		}
	}
}
