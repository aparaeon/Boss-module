package gg.mmorealms.module.core.common.dto.event.server;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import gg.mmorealms.module.core.common.dto.ServerList;
import lombok.Getter;

import java.util.List;

@Getter
public class ServerListBroadcast extends NetworkBroadcast {

	private final ServerList serverList;

	@SuppressWarnings("unused")
	public ServerListBroadcast(ServerList serverList) {
		super();
		this.serverList = serverList;
	}

	public ServerListBroadcast(List<ServerList.ServerEntry> serverList) {
		super();
		this.serverList = new ServerList(serverList);
	}

}
