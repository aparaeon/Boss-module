package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.battles.runner.graal.GraalShowdownService;
import com.raduvoinea.utils.logger.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(GraalShowdownService.class)
public abstract class GraalShowdownServiceMixin {

	@Inject(method = "boot", at = @At("TAIL"), remap = false)
	private void mmoRealmsBoss$injectBossArmor(CallbackInfo callbackInfo) {
		GraalShowdownService self = (GraalShowdownService) (Object) this;
		try (InputStream stream = GraalShowdownServiceMixin.class.getResourceAsStream("/boss_armor.js")) {
			if (stream == null) {
				Logger.error("boss_armor.js missing from boss-module resources; boss damage scaling disabled.");
				return;
			}
			String patch = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			self.getContext().eval("js", patch);
		} catch (IOException | RuntimeException exception) {
			Logger.error("Failed to inject boss_armor.js into Showdown context: " + exception.getMessage());
		}
	}
}
