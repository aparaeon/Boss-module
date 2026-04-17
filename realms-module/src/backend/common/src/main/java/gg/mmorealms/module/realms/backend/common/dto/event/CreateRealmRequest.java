package gg.mmorealms.module.realms.backend.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import gg.mmorealms.module.realms.backend.common.dto.RealmType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("FieldMayBeFinal")
@Getter
public class CreateRealmRequest extends NetworkRequest<UUID> {

	private @NotNull UUID ownerUUID;
	private RealmType realmType;

	public CreateRealmRequest(@NotNull String redisID, @NotNull UUID ownerUUID, RealmType realmType) {
		super(redisID);
		this.ownerUUID = ownerUUID;
		this.realmType = realmType;
	}

}
