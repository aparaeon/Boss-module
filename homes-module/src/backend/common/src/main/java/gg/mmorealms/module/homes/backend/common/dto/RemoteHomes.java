package gg.mmorealms.module.homes.backend.common.dto;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class RemoteHomes extends UUIDRemoteObject<IHomes> implements IHomes {
	public RemoteHomes(@NotNull UUID uuid, @NotNull String server) {
		super(IHomes.class, uuid, server);
	}

	@Override
	public List<Home> getHomes() {
		return sendRequest();
	}

	@Override
	public Home get(String name) {
		return sendRequest(name);
	}

	public Home get(int index) throws IndexOutOfBoundsException {
		return sendRequest(index);
	}

	@Override
	public void add(@NotNull Home home) {
		sendRequest(home);
	}

	@Override
	public Boolean remove(String name) {
		return sendRequest(name);
	}

	@Override
	public void removeAll() {
		sendRequest();
	}

	@Override
	public Boolean has(String name) {
		return sendRequest(name);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public int size() {
		return sendRequest();
	}

	public Boolean isEmpty() {
		return sendRequest();
	}

	public String toPrettyString() {
		return sendRequest();
	}

	public List<String> getNames() {
		return sendRequest();
	}
}
