package gg.mmorealms.module.gyms.backend.fabric.mixin.bag;

import com.cobblemon.mod.common.item.RevivalHerbItem;
import com.cobblemon.mod.common.item.berry.HealingBerryItem;
import com.cobblemon.mod.common.item.berry.PPRestoringBerryItem;
import com.cobblemon.mod.common.item.berry.PortionHealingBerryItem;
import com.cobblemon.mod.common.item.berry.StatusCuringBerryItem;
import com.cobblemon.mod.common.item.interactive.*;
import gg.mmorealms.module.gyms.backend.fabric.helper.MixinCommon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BerryJuiceItem.class, ElixirItem.class, EnergyRootItem.class, EtherItem.class, HealingBerryItem.class,
		HealPowderItem.class, PortionHealingBerryItem.class, PotionItem.class, PPRestoringBerryItem.class, RemedyItem.class,
		RevivalHerbItem.class, StatusCureItem.class, StatusCuringBerryItem.class})
public class PokemonSelectingItemsMixin {

	@Inject(method = "use(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/InteractionResultHolder;",
			at = @At("HEAD"),
			cancellable = true)
	public void onInvoke(ServerPlayer player, ItemStack stack, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (!MixinCommon.bagClauseVerification(player)) {
			cir.setReturnValue(InteractionResultHolder.fail(stack));
			cir.cancel();
		}
	}

}