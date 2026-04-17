package gg.mmorealms.module.core.backend.common.dto.cooldown;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteBackendCooldowns extends UUIDRemoteObject<IBackendCooldowns> implements IBackendCooldowns {
	public RemoteBackendCooldowns(@NotNull UUID uuid, @NotNull String server) {
		super(IBackendCooldowns.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public void set(String type, long time) {
		sendRequest(type, time);
	}

	@Override
	public Long get(@NotNull String id) {
		return sendRequest(id);
	}

}
