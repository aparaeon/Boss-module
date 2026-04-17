package gg.mmorealms.module.essentials.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserLastNetworkLocationRequest extends NetworkRequest<NetworkLocation> {
	private final UUID uuid;

	public UserLastNetworkLocationRequest(UUID uuid) {
		super();
		this.uuid = uuid;
	}
}
