package gg.mmorealms.module.essentials.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJoinMessageEvent extends NetworkBroadcast {
	private String name;
}
