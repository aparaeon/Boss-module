package gg.mmorealms.module.mega_evolution.backend.fabric.mixin;


import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ShowdownActionRequest;
import com.cobblemon.mod.common.battles.ShowdownMoveset;
import gg.mmorealms.module.mega_evolution.backend.fabric.utils.MegaEvolutionUtils;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = ShowdownActionRequest.class, remap = false)
public class ShowdownActionRequestMixin {

	@Unique
	private final ShowdownActionRequest self = (ShowdownActionRequest) (Object) this;

	@Inject(method = "sanitize", at = @At("HEAD"), cancellable = true)
	private void handleGimmicks(PokemonBattle battle, BattleActor battleActor, CallbackInfo ci) {
		ci.cancel();
		mega_evolution$handleMegaEvolution(battle, battleActor);
	}

	@Unique
	private void mega_evolution$handleMegaEvolution(PokemonBattle battle, BattleActor battleActor) {
		ServerPlayer player = null;

		for (ServerPlayer battlePlayer : battle.getPlayers()) {
			if (battlePlayer.getUUID().equals(battleActor.getUuid())) {
				player = battlePlayer;
				break;
			}
		}

		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		List<ShowdownMoveset> active = self.getActive();
		if (active == null) {
			return;
		}

		active.forEach(moveset -> {
			moveset.getGimmicks().forEach(gimmick -> {
				// TODO will need to change logic if we gonna add more gimmicks
				switch (gimmick) {
					case MEGA_EVOLUTION -> {
						// Showdown doesn't know of megas evolved outside of battle, so we need to check manually
						if (MegaEvolutionUtils.hasMegaPokemonInParty(serverPlayer) || !MegaEvolutionUtils.holdsMegaBracelet(serverPlayer)) {
							moveset.blockGimmick(gimmick);
						}
					}
					default -> moveset.blockGimmick(gimmick);
				}
			});
		});
	}
}
