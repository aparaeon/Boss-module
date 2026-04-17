package gg.mmorealms.module.chat.common.dto;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;

@Getter
public class GlobalMessageEvent extends NetworkEvent {
	private final String message;

	public GlobalMessageEvent(String message) {
		super();
		this.message = message;
	}
}
