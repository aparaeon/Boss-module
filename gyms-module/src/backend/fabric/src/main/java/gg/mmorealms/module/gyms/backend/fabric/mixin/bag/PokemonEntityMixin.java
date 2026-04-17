package gg.mmorealms.module.gyms.backend.fabric.mixin.bag;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.gyms.backend.fabric.helper.MixinCommon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
class PokemonEntityMixin {

	@Inject(method = "attemptItemInteraction", at = @At("HEAD"), cancellable = true, remap = false)
	public void onInvoke(Player player, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.isEmpty()) {
			cir.setReturnValue(false);
			cir.cancel();
		}

		if (!MixinCommon.bagClauseVerification(player)) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
