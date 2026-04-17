package gg.mmorealms.module.core.common.dto;

import gg.mmorealms.loader.common.dto.ServerType;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ServerList {
	private final List<ServerEntry> serversList;

	public List<ServerEntry> getList() {
		return serversList;
	}

	public ServerList() {
		this(new ArrayList<>());
	}

	public @Nullable String getLowestUsageServer(ServerType serverType) {
		int lowest = Integer.MAX_VALUE;
		String serverID = null;

		for (ServerEntry server : serversList) {
			if (server.type() == serverType && server.playerCount() < lowest) {
				lowest = server.playerCount();
				serverID = server.serverID();
			}
		}

		return serverID;
	}

	public record ServerEntry(ServerType type, String serverID, int playerCount) {
	}
}
