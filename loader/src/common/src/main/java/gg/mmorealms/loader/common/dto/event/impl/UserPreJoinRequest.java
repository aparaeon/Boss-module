package gg.mmorealms.loader.common.dto.event.impl;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.event.network.NetworkRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserPreJoinRequest extends NetworkRequest<Boolean> {

	private final @Getter UUID uuid;
	private final List<String> joinEvents;

	public UserPreJoinRequest(String target, UUID uuid) {
		this(target, uuid, new ArrayList<>());
	}

	public UserPreJoinRequest(String target, UUID uuid, List<String> joinEvents) {
		super(target);
		this.uuid = uuid;
		this.joinEvents = new ArrayList<>(joinEvents);
	}

	public void addJoinEvent(RedisRequest event) {
		event.setTarget(this.getTarget());
		joinEvents.add(this.getRedisManager().getGsonHolder().value().toJson(event));
	}

	public void addJoinEvent(String serializedEvent) {
		this.joinEvents.add(serializedEvent);
	}

	public List<RedisRequest<?>> getJoinEvents(String overwriteTarget) {
		List<RedisRequest<?>> events = new ArrayList<>();
		for (String event : joinEvents) {
			RedisRequest<?> eventToAdd = CommonLoader.instance().fromJson(event, RedisRequest.class);

			eventToAdd.setTarget(overwriteTarget);
			events.add(eventToAdd);
		}
		return events;
	}

}
