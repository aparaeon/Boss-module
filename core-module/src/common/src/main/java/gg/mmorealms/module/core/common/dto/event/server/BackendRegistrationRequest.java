package gg.mmorealms.module.core.common.dto.event.server;

import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BackendRegistrationRequest extends NetworkRequest<Boolean> {

	private final String host;
	private final int port;
	private final ServerType serverType;
	private final boolean firstRegistration;

}
