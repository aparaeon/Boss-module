package gg.mmorealms.module.pokemon.backend.fabric.mixin;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO: Temporary fix for poke-market trades. Disable at some point
@Mixin(AbstractVillager.class)
public class VillagerMixin {

	@Inject(method = "getOffers", at = @At("HEAD"), cancellable = true)
	private void onGetOffers(CallbackInfoReturnable<MerchantOffers> cir) {
		cir.setReturnValue(new MerchantOffers());
		cir.cancel();
	}
}
