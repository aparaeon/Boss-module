package gg.mmorealms.module.core.common.dto.chat;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatCaptureResultEvent extends NetworkEvent {

	private UUID uuid;
	private String input;

	public ChatCaptureResultEvent(String target, UUID uuid, String input) {
		super(target);
		this.uuid = uuid;
		this.input = input;
	}

}
