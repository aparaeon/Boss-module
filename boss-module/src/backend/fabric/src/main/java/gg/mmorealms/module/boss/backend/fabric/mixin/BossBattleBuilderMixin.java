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
 * Forces {@code fleeDistance = -1F} on the wild actor when the wild Pokémon is a boss.
 * Cobblemon's {@code PokemonBattle.checkFlee()} short-circuits to "cannot flee" on -1F.
 * {@code PokemonBattleActor.fleeDistance} is a Kotlin {@code val} — set once via constructor;
 * we intercept the {@code PokemonBattleActor} constructor invocation inside {@code BattleBuilder.pve}.
 *
 * Descriptors verified via javap against compiled Cobblemon:
 *   - {@code BattleBuilder.pve} main descriptor uses {@code Lcom/cobblemon/mod/common/battles/BattleFormat;}
 *     (NOT the {@code api/battles/model} package).
 *   - {@code PokemonBattleActor} lives at {@code com/cobblemon/mod/common/battles/actor/PokemonBattleActor}.
 *   - Constructor descriptor: {@code (UUID, BattlePokemon, float, BattleAI)V}.
 *   - {@code index = 2} → {@code float fleeDistance} (UUID=0, BattlePokemon=1, float=2, BattleAI=3).
 *
 * During local bring-up only, you may temporarily add {@code require = 0} to allow the module
 * to boot even if the mixin fails to bind. Do NOT ship that — anti-flee is core to the feature.
 * Default {@code require = 1} (fail-loud at boot) is the correct production setting.
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
