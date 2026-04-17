package gg.mmorealms.module.wild.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.core.backend.common.dto.LevelType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TeleportToRandomLocationEvent extends NetworkEvent {

	private final UUID uuid;
	private final LevelType levelType;

	public TeleportToRandomLocationEvent(UUID uuid, LevelType levelType) {
		this.uuid = uuid;
		this.levelType = levelType;
	}
}
