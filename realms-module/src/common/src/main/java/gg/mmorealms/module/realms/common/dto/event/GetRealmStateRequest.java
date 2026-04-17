package gg.mmorealms.module.realms.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import gg.mmorealms.module.realms.common.dto.RealmState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class GetRealmStateRequest extends NetworkRequest<RealmState> {

	private UUID ownerUUID;

	public GetRealmStateRequest(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}
}
