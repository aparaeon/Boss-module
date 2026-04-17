package gg.mmorealms.loader.backend.common.mixin;

import gg.mmorealms.loader.backend.common.dto.event.fabric.entity.MinecartTickEvent;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
@Getter
public class MinecartEntityMixin {

	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void onTick(CallbackInfo callbackInfo) {
		if ("true".equalsIgnoreCase(SecretsUtils.getEnvironmentVariable("DISABLE_MINECART_CRASH_FIX"))) {
			return;
		}

		AbstractMinecart minecart = (AbstractMinecart) (Object) this;
		boolean result = new MinecartTickEvent(minecart).fireSync();
		if (!result) {
			minecart.remove(Entity.RemovalReason.DISCARDED);
		}
	}
}
