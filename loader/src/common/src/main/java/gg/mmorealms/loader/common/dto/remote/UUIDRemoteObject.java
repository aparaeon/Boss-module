package gg.mmorealms.loader.common.dto.remote;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class UUIDRemoteObject<ObjectInterface> extends RemoteObject<UUID, ObjectInterface> {

	public UUIDRemoteObject(@NotNull Class<ObjectInterface> objectInterfaceClass, @NotNull UUID uuid, @NotNull String server) {
		super(UUID.class, objectInterfaceClass, uuid, server);
	}

	public @NotNull UUID getUUID() {
		return getIdentifier();
	}
}
