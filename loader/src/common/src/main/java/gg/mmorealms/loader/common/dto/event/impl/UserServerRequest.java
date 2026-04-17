package gg.mmorealms.loader.common.dto.event.impl;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class UserServerRequest extends NetworkRequest<String> {

	private @Nullable UUID uuid;
	private @Nullable String username;

	public UserServerRequest(@NotNull UUID uuid) {
		super();
		this.uuid = uuid;
	}

	public UserServerRequest(@NotNull String username) {
		super();
		this.username = username;
	}
}
