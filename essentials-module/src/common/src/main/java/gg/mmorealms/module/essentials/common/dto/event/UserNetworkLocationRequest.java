package gg.mmorealms.module.essentials.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class UserNetworkLocationRequest extends NetworkRequest<NetworkLocation> {
	private final UUID uuid;

	public UserNetworkLocationRequest(@NotNull String redisID, UUID uuid) {
		super(redisID);
		this.uuid = uuid;
	}
}
