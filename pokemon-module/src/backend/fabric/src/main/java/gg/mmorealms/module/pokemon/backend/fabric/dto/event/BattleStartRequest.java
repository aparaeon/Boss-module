package gg.mmorealms.module.pokemon.backend.fabric.dto.event;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import gg.mmorealms.module.pokemon.common.dto.Response;
import lombok.Getter;

@Getter
public class BattleStartRequest extends LocalRequest<Response> {

	private final PokemonBattle battle;

	public BattleStartRequest(BattleStartedEvent.Pre nativeEvent) {
		super(new Response(true, "Battle can start"));
		this.battle = nativeEvent.getBattle();
	}
}
