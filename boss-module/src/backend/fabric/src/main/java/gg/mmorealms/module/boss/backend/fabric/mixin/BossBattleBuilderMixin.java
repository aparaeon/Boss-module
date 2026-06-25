package gg.mmorealms.module.boss.backend.fabric.mixin;

import com.cobblemon.mod.common.api.battles.model.ai.BattleAI;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.battles.ai.RandomBattleAI;
import com.cobblemon.mod.common.battles.ai.StrongBattleAI;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.BattleCloneProperty;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import gg.mmorealms.module.boss.backend.fabric.BossFabricModule;
import gg.mmorealms.module.boss.backend.fabric.manager.BossManager.NbtKeys;
import gg.mmorealms.module.boss.backend.fabric.manager.BossMovesetPlanner;
import gg.mmorealms.module.boss.common.BossTier;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(BattleBuilder.class)
public abstract class BossBattleBuilderMixin {

	private static final String PVE_DESC =
			"pve(Lnet/minecraft/class_3222;Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZFLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;";
	private static final String WILD_ACTOR_NEW =
			"(Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;FLcom/cobblemon/mod/common/api/battles/model/ai/BattleAI;ILkotlin/jvm/internal/DefaultConstructorMarker;)Lcom/cobblemon/mod/common/battles/actor/PokemonBattleActor;";

	private static final int BOSS_BATTLE_LEVEL = 100;

	@Redirect(method = PVE_DESC, at = @At(value = "NEW", target = WILD_ACTOR_NEW), remap = false)
	private PokemonBattleActor mmoRealmsBoss$upgradeBossAI(
			UUID uuid, BattlePokemon pokemon, float fleeDistance, BattleAI defaultedAI,
			int defaultMask, DefaultConstructorMarker marker, ServerPlayer player
	) {
		Pokemon original = pokemon.getOriginalPokemon();
		if (original.getPersistentData().getBoolean(NbtKeys.BOSS)
				&& original.getPersistentData().contains(NbtKeys.TIER)) {
			return new PokemonBattleActor(uuid, mmoRealmsBoss$inflateBoss(original, player), fleeDistance, new StrongBattleAI(5));
		}
		return new PokemonBattleActor(uuid, pokemon, fleeDistance, new RandomBattleAI());
	}

	private static BattlePokemon mmoRealmsBoss$inflateBoss(Pokemon original, ServerPlayer player) {
		RegistryAccess registryAccess = BossFabricModule.instance().getServer().registryAccess();
		Pokemon battleClone = original.clone(true, registryAccess);
		BattleCloneProperty.INSTANCE.isBattleClone().apply(battleClone);
		UncatchableProperty.INSTANCE.uncatchable().apply(battleClone);

		BossTier tier = BossTier.valueOf(original.getPersistentData().getString(NbtKeys.TIER));
		List<String> adaptiveMoves = BossMovesetPlanner.planAdaptive(
				tier, original.getSpecies(), original.getLevel(), mmoRealmsBoss$snapshot(player));
		if (!adaptiveMoves.isEmpty()) {
			PokemonProperties moveProperties = new PokemonProperties();
			moveProperties.setMoves(adaptiveMoves);
			moveProperties.apply(battleClone);
		}

		battleClone.setLevel(BOSS_BATTLE_LEVEL);
		battleClone.heal();
		return new BattlePokemon(original, battleClone, entity -> kotlin.Unit.INSTANCE);
	}

	private static BossMovesetPlanner.PlayerSnapshot mmoRealmsBoss$snapshot(ServerPlayer player) {
		List<ElementalType> leadTypes = new ArrayList<>();
		PlayerPartyStore party = PlayerExtensionsKt.party(player);
		Pokemon lead = null;
		for (Pokemon member : party) {
			if (member.getCurrentHealth() > 0) {
				lead = member;
				break;
			}
		}
		if (lead != null) {
			for (ElementalType type : lead.getTypes()) {
				leadTypes.add(type);
			}
		}
		return new BossMovesetPlanner.PlayerSnapshot(leadTypes);
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
