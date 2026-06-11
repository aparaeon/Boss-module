package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.boss.backend.fabric.manager.BossNbtKeys;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.UUID;

/**
 * Forces {@code fleeDistance = -1F} on the wild actor for bosses — Cobblemon's checkFlee returns "no" on -1F.
 * Intercepts the {@code PokemonBattleActor} constructor (index=2 = float fleeDistance) inside {@code BattleBuilder.pve}.
 * Descriptors verified via javap against the compiled Cobblemon jar — keep require=1 (fail-loud at boot).
 */
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
		if (wild != null && wild.getPokemon().getPersistentData().getBoolean(BossNbtKeys.BOSS)) {
			return -1F;
		}
		return fleeDistanceArg;
	}
}
