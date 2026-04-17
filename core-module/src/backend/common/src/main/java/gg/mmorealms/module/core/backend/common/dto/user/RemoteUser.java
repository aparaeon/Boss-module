package gg.mmorealms.module.core.backend.common.dto.user;

import com.raduvoinea.utils.generic.dto.Range;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteUser extends UUIDRemoteObject<IUser> implements IUser {
	public RemoteUser(@NotNull UUID uuid, @NotNull String server) {
		super(IUser.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public void setUsername(String username) {
		sendRequest(username);
	}

	@Override
	public String getUsername() {
		return sendRequest();
	}

	@Override
	public void sendMessage(String message) {
		sendRequest(message);
	}

	@Override
	public void sendActionMessage(String message) {
		sendRequest(message);
	}

	@Override
	public Boolean hasPermission(@NotNull String permission) {
		return sendRequest(permission);
	}

	@Override
	public Integer getCountPermission(@NotNull String basePermission, Range range) {
		return sendRequest(basePermission, range);
	}

	@Override
	public Location getBlockLocation() {
		return sendRequest();
	}

	@Override
	public Location getLocation() {
		return sendRequest();
	}

	@Override
	public void kick(String message) {
		sendRequest(message);
	}
}
