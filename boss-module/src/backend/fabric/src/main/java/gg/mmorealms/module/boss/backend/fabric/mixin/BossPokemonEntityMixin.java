package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(PokemonEntity.class)
public abstract class BossPokemonEntityMixin {

	@Inject(
			method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mmoRealmsBoss$blockBossDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		PokemonEntity self = (PokemonEntity) (Object) this;
		if (self.getPokemon().getPersistentData().getBoolean(NbtKeys.BOSS)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(
			method = "canBeLeashed()Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mmoRealmsBoss$blockBossLeash(CallbackInfoReturnable<Boolean> cir) {
		PokemonEntity self = (PokemonEntity) (Object) this;
		if (self.getPokemon().getPersistentData().getBoolean(NbtKeys.BOSS)) {
			cir.setReturnValue(false);
		}
	}
}