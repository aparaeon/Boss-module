package gg.mmorealms.loader.backend.common.mixin;

import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerDropItemEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class ServerPlayerMixin {
	@Shadow
	public abstract boolean addItem(ItemStack stack);


	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
	private void onDropItem(ItemStack droppedItem, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (player.isRemoved()) {
			return;
		}

		if (!new PlayerDropItemEvent(player, droppedItem).fireSync()) {
			cir.setReturnValue(null);
			addItem(droppedItem);
			cir.cancel();
		}
	}
}
