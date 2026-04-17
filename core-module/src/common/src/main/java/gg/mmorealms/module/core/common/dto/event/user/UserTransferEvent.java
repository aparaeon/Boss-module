package gg.mmorealms.module.core.common.dto.event.user;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.core.common.dto.server_location.ExactServerLocation;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class UserTransferEvent extends NetworkEvent {

	private final UUID uuid;
	private final IServerLocation server;
	private final List<String> joinEvents = new ArrayList<>();
	private final boolean saveLocation;
	private final boolean sendSameServerMessage;

	public UserTransferEvent(@NotNull UUID uuid, @NotNull IServerLocation server, boolean saveLocation, boolean sendSameServerMessage, @NotNull RedisRequest... events) {
		super(CommonLoader.getProxyID());

		this.uuid = uuid;
		this.server = server;
		this.sendSameServerMessage = sendSameServerMessage;

		for (RedisRequest event : events) {
			joinEvents.add(CommonLoader.instance().toJson(event));
		}

		this.saveLocation = saveLocation;
	}

	public UserTransferEvent(@NotNull UUID uuid, @NotNull IServerLocation server, boolean saveLocation, @NotNull RedisRequest... events) {
		this(uuid, server, true, true, events);
	}


	// TODO Add support for any type of event not just RedisEvents
	public UserTransferEvent(@NotNull UUID uuid, @NotNull IServerLocation server, @NotNull RedisRequest... events) {
		this(uuid, server, true, events);

	}

	public UserTransferEvent(@NotNull UUID uuid, @NotNull String server, @NotNull RedisRequest... events) {
		this(uuid, new ExactServerLocation(server), events);
	}
}
