package gg.mmorealms.module.core.common.dto.event.server;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import org.jetbrains.annotations.NotNull;

public class Heartbeat extends NetworkRequest<Boolean> {

	public Heartbeat() {
		super();
	}

	public Heartbeat(@NotNull String redisID) {
		super(redisID);
	}

}
