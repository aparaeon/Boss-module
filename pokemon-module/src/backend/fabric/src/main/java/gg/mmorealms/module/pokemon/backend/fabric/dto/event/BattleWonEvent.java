package gg.mmorealms.module.pokemon.backend.fabric.dto.event;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class BattleWonEvent extends LocalEvent {

	private final List<BattleActor> winners;
	private final List<BattleActor> losers;

	public BattleWonEvent(BattleVictoryEvent nativeEvent) {
		this.losers = nativeEvent.getLosers();
		this.winners = nativeEvent.getWinners();
	}

	public BattleWonEvent(PokemonBattle battle) {
		this.losers = battle.getLosers();
		this.winners = battle.getWinners();
	}
}
