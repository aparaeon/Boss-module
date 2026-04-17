package gg.mmorealms.module.hunts.backend.common.events;

import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ActiveHuntCompletedEvent extends HuntEvent {
	private final IPokemon capturedPokemon;

	public ActiveHuntCompletedEvent(Hunts hunts, HuntType type, UUID playerUUID, IPokemon capturedPokemon) {
		super(hunts, type, playerUUID);
		this.capturedPokemon = capturedPokemon;
	}
}
