package gg.mmorealms.module.realms.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class LoadRealmEvent extends NetworkEvent {
	private final UUID requesterUUID;
	private final UUID ownerUUID;

	public LoadRealmEvent(String server, UUID requesterUUID, UUID ownerUUID) {
		super(server);
		this.requesterUUID = requesterUUID;
		this.ownerUUID = ownerUUID;
	}

	public LoadRealmEvent(UUID requesterUUID, UUID ownerUUID) {
		this.requesterUUID = requesterUUID;
		this.ownerUUID = ownerUUID;
	}

	public LoadRealmEvent(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
		this.requesterUUID = ownerUUID;
	}

	public LoadRealmEvent copy(){
		return new LoadRealmEvent(this.getTarget(), this.requesterUUID, this.ownerUUID);
	}
}
