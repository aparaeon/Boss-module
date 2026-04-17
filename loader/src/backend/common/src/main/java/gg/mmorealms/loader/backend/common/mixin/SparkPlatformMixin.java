package gg.mmorealms.loader.backend.common.mixin;

import gg.mmorealms.loader.backend.common.BackendLoader;
import me.lucko.spark.common.SparkPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SparkPlatform.class)
public class SparkPlatformMixin {

	@Inject(method = "enable", at = @At(value = "RETURN"), remap = false)
	private void onInit(CallbackInfo ci) {
		BackendLoader.instance().setSparkPlatform((SparkPlatform) (Object) this);
	}

}
