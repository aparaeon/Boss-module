package gg.mmorealms.module.core.common.dto.event.user;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.loader.common.dto.location.ILocation;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserTeleportEvent extends NetworkEvent {

	private final UUID uuid;
	private final ILocation location;

	public UserTeleportEvent(@NotNull UUID uuid, @NotNull ILocation location) {
		this(null, uuid, location);
	}

	public UserTeleportEvent(String target, @NotNull UUID uuid, @NotNull ILocation location) {
		super(target);
		this.uuid = uuid;
		this.location = location;
	}

}
