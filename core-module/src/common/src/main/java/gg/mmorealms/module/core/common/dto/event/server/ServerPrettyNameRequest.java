package gg.mmorealms.module.core.common.dto.event.server;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerPrettyNameRequest extends NetworkRequest<String> {
	private String serverID;
}
