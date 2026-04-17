package gg.mmorealms.module.pokemon.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SetPokemonEvent extends NetworkEvent {

	private UUID targetUUID;
	private int slot;
	private IPokemon pokemon;

	public SetPokemonEvent(String target, UUID targetUUID, int slot, IPokemon pokemon) {
		super(target);
		this.targetUUID = targetUUID;
		this.slot = slot;
		this.pokemon = pokemon;
	}

}
