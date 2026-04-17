package gg.mmorealms.module.core.common.dto.event.server;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import gg.mmorealms.module.core.common.dto.PlayerList;
import lombok.Getter;

import java.util.List;

@Getter
public class PlayerListBroadcast extends NetworkBroadcast {

	private final PlayerList playerList;

	public PlayerListBroadcast(PlayerList playerList) {
		super();
		this.playerList = playerList;
	}

	public PlayerListBroadcast(List<PlayerList.PlayerEntry> playerList) {
		super();
		this.playerList = new PlayerList(playerList);
	}
}