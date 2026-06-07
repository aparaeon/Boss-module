package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.manager.BossNbtKeys;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hard-block damage to boss Pokémon entities at the top of {@code hurt()}, returning {@code false}
 * before any vanilla / Cobblemon damage logic runs. The vanilla {@code Invulnerable} NBT flag has
 * several documented bypass paths ({@code BYPASSES_INVULNERABILITY} damage type tag, creative-mode
 * /kill, certain mod damage sources); this mixin guarantees boss bosses are untouchable outside
 * the Cobblemon battle engine — which mutates {@code Pokemon.currentHealth} directly and never
 * routes through {@code entity.hurt()}, so battles are unaffected.
 * <p>
 * Cancel via {@code cir.setReturnValue(false)} at HEAD — runs before {@code super.hurt} and before
 * Cobblemon's own {@code beamMode}/{@code busyLocks}/config-flag short-circuits.
 */
@Mixin(value = PokemonEntity.class, remap = false)
public abstract class BossPokemonEntityMixin {

	@Inject(
			method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	private void mmoRealmsBoss$blockBossDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		PokemonEntity self = (PokemonEntity) (Object) this;
		if (self.getPokemon().getPersistentData().getBoolean(BossNbtKeys.BOSS)) {
			cir.setReturnValue(false);
		}
	}
}
