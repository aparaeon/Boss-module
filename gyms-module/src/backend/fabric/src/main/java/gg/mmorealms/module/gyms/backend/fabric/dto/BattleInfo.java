package gg.mmorealms.module.gyms.backend.fabric.dto;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;

public record BattleInfo(PokemonBattle battle, Gym gym, long forcedEndTime) {
	public BattleInfo(PokemonBattle battle, Gym gym, Time forcedEndTime) {
		this(battle, gym, System.currentTimeMillis() + forcedEndTime.toMilliseconds());
	}
}
