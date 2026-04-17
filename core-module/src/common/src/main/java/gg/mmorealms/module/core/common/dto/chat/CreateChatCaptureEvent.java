package gg.mmorealms.module.core.common.dto.chat;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateChatCaptureEvent extends NetworkEvent {

	private UUID uuid;

	public CreateChatCaptureEvent(UUID uuid) {
		this.uuid = uuid;
	}

}
