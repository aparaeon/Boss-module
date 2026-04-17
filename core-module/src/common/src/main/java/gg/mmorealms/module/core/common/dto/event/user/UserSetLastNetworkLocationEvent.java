package gg.mmorealms.module.core.common.dto.event.user;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserSetLastNetworkLocationEvent extends NetworkEvent {
	private final UUID uuid;
	private final NetworkLocation networkLocation;

	public UserSetLastNetworkLocationEvent(UUID uuid, NetworkLocation networkLocation) {
		super();
		this.uuid = uuid;
		this.networkLocation = networkLocation;
	}
}
