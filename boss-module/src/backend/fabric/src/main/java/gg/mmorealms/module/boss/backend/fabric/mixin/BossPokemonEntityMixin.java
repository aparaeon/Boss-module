package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.manager.BossNbtKeys;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Cancels {@code hurt()} at HEAD for boss Pokémon. Do NOT add {@code remap = false} to @Mixin — it breaks descriptor remap on the @Inject and the injection silently fails to attach. */
@Mixin(PokemonEntity.class)
public abstract class BossPokemonEntityMixin {

	@Inject(
			method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mmoRealmsBoss$blockBossDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// Deliberately blocks ALL damage including /kill and void — bosses die only to battles
		// or /boss admin despawn.
		PokemonEntity self = (PokemonEntity) (Object) this;
		if (self.getPokemon().getPersistentData().getBoolean(BossNbtKeys.BOSS)) {
			cir.setReturnValue(false);
		}
	}
}
