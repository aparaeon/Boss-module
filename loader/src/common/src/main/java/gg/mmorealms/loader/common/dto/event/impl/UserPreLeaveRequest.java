package gg.mmorealms.loader.common.dto.event.impl;

import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserPreLeaveRequest extends NetworkRequest<Boolean> {

	private final UUID uuid;

	public UserPreLeaveRequest(String target, UUID uuid) {
		super(target);
		this.uuid = uuid;
	}

}
