package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.cobblemon.mod.common.battles.ai.StrongBattleAI;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(BattleBuilder.class)
public abstract class BossBattleBuilderMixin {

	private static final String PVE_DESC =
			"pve(Lnet/minecraft/class_3222;Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZFLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;";
	private static final String WILD_ACTOR_NEW =
			"(Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;FLcom/cobblemon/mod/common/api/battles/model/ai/BattleAI;ILkotlin/jvm/internal/DefaultConstructorMarker;)Lcom/cobblemon/mod/common/battles/actor/PokemonBattleActor;";

	@Redirect(method = PVE_DESC, at = @At(value = "NEW", target = WILD_ACTOR_NEW), remap = false)
	private PokemonBattleActor mmoRealmsBoss$upgradeBossAI(
			UUID uuid, BattlePokemon pokemon, float fleeDistance, BattleAI defaultedAI,
			int defaultMask, DefaultConstructorMarker marker
	) {
		Pokemon original = pokemon.getOriginalPokemon();
		if (original.getPersistentData().getBoolean(NbtKeys.BOSS)
				&& original.getPersistentData().contains(NbtKeys.TIER)) {
			return new PokemonBattleActor(uuid, pokemon, fleeDistance, new StrongBattleAI(5));
		}
		return new PokemonBattleActor(uuid, pokemon, fleeDistance, new RandomBattleAI());
	}
}