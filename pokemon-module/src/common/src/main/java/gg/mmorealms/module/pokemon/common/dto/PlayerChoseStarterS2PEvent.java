package gg.mmorealms.module.pokemon.common.dto;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerChoseStarterS2PEvent extends NetworkEvent {

	public UUID uuid;

	public PlayerChoseStarterS2PEvent(UUID uuid) {
		super();
		this.uuid = uuid;
	}

}
