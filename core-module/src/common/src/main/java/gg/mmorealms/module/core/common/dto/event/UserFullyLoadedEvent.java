package gg.mmorealms.module.core.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserFullyLoadedEvent extends NetworkEvent {

	private UUID uuid;

	public UserFullyLoadedEvent(UUID uuid){
		super();
		this.uuid = uuid;
	}

}
