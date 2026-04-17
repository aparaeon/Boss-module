package gg.mmorealms.module.essentials.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserFirstJoinEvent extends NetworkEvent {
	private String username;
}
