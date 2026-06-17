package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = BattleBuilder.class, remap = false)
public abstract class BossBattleBuilderMixin {

	@ModifyArg(
			method = "pve(Lnet/minecraft/server/level/ServerPlayer;Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZFLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;",
			at = @At(
					value = "INVOKE",
					target = "Lcom/cobblemon/mod/common/battles/actor/PokemonBattleActor;<init>(Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;FLcom/cobblemon/mod/common/api/battles/model/ai/BattleAI;)V"
			),
			index = 2,
			remap = false
	)
	private float mmoRealmsBoss$forceNoFlee(
			float fleeDistanceArg,
			ServerPlayer player,
			PokemonEntity wild,
			UUID leadingPokemon,
			BattleFormat format,
			boolean cloneParties,
			boolean healFirst,
			float fleeDistance,
			PartyStore party
	) {
		if (wild != null && wild.getPokemon().getPersistentData().getBoolean(NbtKeys.BOSS)) {
			return -1F;
		}
		return fleeDistanceArg;
	}
}
@Mixin(PokemonEntity.class)
abstract class BossPokemonEntityMixin {

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
}
