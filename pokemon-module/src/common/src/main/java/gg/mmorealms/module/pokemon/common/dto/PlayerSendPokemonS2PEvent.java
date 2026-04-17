package gg.mmorealms.module.pokemon.common.dto;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerSendPokemonS2PEvent extends NetworkEvent {

	public UUID uuid;

	public PlayerSendPokemonS2PEvent(UUID uuid) {
		super();
		this.uuid = uuid;
	}

}
