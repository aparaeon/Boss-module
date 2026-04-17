package gg.mmorealms.module.gyms.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RemoteUserGymRecord extends UUIDRemoteObject<IUserGymRecord> implements IUserGymRecord {
	public RemoteUserGymRecord(@NotNull UUID uuid, @NotNull String server) {
		super(IUserGymRecord.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	public String toPrettyString(Boolean showHeader, Boolean showUnknownCurrencies) {
		return sendRequest(showHeader, showUnknownCurrencies);
	}

	@Override
	public void set(String id, GymRecord gymRecord) {
		sendRequest(id, gymRecord);
	}

	@Override
	public @Nullable GymRecord getGymRecord(String id) {
		return sendRequest(id);
	}

	@Override
	public void delete() {
		sendRequest();
	}
}