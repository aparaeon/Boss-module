package gg.mmorealms.module.core.common.dto.event.user;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class IsValidUserRequest extends NetworkRequest<Boolean> {
	private final String userName;

	public IsValidUserRequest(@NotNull String redisID, String userName) {
		super(redisID);
		this.userName = userName;
	}
}
