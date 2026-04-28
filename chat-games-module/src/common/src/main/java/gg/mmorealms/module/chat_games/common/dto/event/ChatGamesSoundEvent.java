package gg.mmorealms.module.chat_games.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatGamesSoundEvent extends NetworkEvent {

	private List<UUID> playerUuids;

	public ChatGamesSoundEvent(String targetServerId, List<UUID> playerUuids) {
		super(targetServerId);
		this.playerUuids = playerUuids;
	}

}
