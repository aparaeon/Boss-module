package gg.mmorealms.module.crates.backend.common.database;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteCrateKeys extends UUIDRemoteObject<ICrateKeys> implements ICrateKeys {
	public RemoteCrateKeys(@NotNull UUID uuid, @NotNull String server) {
		super(ICrateKeys.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public Integer getKeys(String id) {
		return sendRequest(id);
	}

	@Override
	public void addKeys(String id, Integer amount) {
		sendRequest(id, amount);
	}
}
