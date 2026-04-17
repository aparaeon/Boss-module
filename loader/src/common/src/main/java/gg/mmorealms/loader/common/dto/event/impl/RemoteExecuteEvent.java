package gg.mmorealms.loader.common.dto.event.impl;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@Getter
public class RemoteExecuteEvent extends NetworkEvent {

	private List<String> serializedEvents;

	public RemoteExecuteEvent(@NotNull String redisID, @NotNull List<String> serializedEvents) {
		super(redisID);
		this.serializedEvents = serializedEvents;
	}

	public void fireAll() {
		for (String event : serializedEvents) {
			RedisRequest<?> request = CommonLoader.instance().fromJson(event, RedisRequest.class);
			request.fireSync();
		}
	}
}
