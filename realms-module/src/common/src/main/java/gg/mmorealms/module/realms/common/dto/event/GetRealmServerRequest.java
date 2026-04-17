package gg.mmorealms.module.realms.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class GetRealmServerRequest extends NetworkRequest<String> {
	private UUID ownerUUID;

	public GetRealmServerRequest(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

}
