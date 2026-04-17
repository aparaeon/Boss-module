package gg.mmorealms.module.realms.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;

import java.util.UUID;

@Getter
public class IsRealmCrashedRequest extends NetworkRequest<Boolean> {
	private final UUID ownerUUID;

	public IsRealmCrashedRequest(UUID ownerUUID) {
		this.setResult(false);
		this.ownerUUID = ownerUUID;
	}
}
