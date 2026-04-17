package gg.mmorealms.module.gyms.backend.fabric.mixin;

import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.manager.GymUtils;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
@Mixin(BattleBuilder.class)
public class BattleBuilderMixin {
	@Redirect(
			method = "pvn(Lnet/minecraft/class_3222;Lcom/cobblemon/mod/common/entity/npc/NPCEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;",
			at = @At(
					value = "INVOKE",
					target = "Lcom/cobblemon/mod/common/pokemon/Pokemon;setLevel(I)V",
					ordinal = 0
			),
			remap = false
	)
	private void capPlayerLevelsOnStore(Pokemon pokemon, int level,
	                                    ServerPlayer player,
	                                    NPCEntity npcEntity) {
		Gym gymByNPC = GymUtils.getGymByNPC(npcEntity);
		if (gymByNPC != null) {
			level = Math.min(level, pokemon.getLevel());
		}

		pokemon.setLevel(level);
	}


	@Redirect(
//			Human readable method declaration (not working because of minecraft):
//			method = "pvn(Lnet/minecraft/server/level/ServerPlayer;Lcom/cobblemon/mod/common/entity/npc/NPCEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;"
			method = "pvn(Lnet/minecraft/class_3222;Lcom/cobblemon/mod/common/entity/npc/NPCEntity;Ljava/util/UUID;Lcom/cobblemon/mod/common/battles/BattleFormat;ZZLcom/cobblemon/mod/common/api/storage/party/PartyStore;)Lcom/cobblemon/mod/common/battles/BattleStartResult;",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;",
					ordinal = 1
			),
			remap = false
	)
	private Iterator<Pokemon> patchGymNpcBattleLevel(
			Iterable<Pokemon> instance,
			ServerPlayer player,
			NPCEntity npcEntity,
			UUID leadingPokemon,
			BattleFormat battleFormat) {
		int adjustLevel = battleFormat.getAdjustLevel();
		if (adjustLevel <= 0) {
			return instance.iterator();
		}

		Gym gymByNPC = GymUtils.getGymByNPC(npcEntity);
		if (gymByNPC == null) {
			return instance.iterator();
		}

		return Collections.emptyIterator();
	}
}
