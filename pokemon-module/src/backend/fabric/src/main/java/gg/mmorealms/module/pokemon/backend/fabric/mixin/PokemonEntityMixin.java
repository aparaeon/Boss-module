package gg.mmorealms.module.pokemon.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public abstract class PokemonEntityMixin {

	@Inject(
			method = "load",
			at = @At(
					value = "INVOKE",
					target = "Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;loadScriptingFromNBT(Lnet/minecraft/nbt/CompoundTag;)V"
			),
			cancellable = true
//			remap = false
	)
	private void load(CompoundTag nbt, CallbackInfo ci) {
		try {
			((PokemonEntity) (Object) this).loadScriptingFromNBT(nbt);
		} catch (Throwable e) {
			ci.cancel();
		}
	}
}
