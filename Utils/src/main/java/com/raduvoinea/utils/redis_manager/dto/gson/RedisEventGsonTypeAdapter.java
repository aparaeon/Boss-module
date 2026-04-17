package com.raduvoinea.utils.redis_manager.dto.gson;

import com.raduvoinea.utils.redis_manager.event.RedisEvent;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;

public class RedisEventGsonTypeAdapter extends GenericRedisRequestGsonTypeAdapter<RedisEvent> {

	public RedisEventGsonTypeAdapter(ClassLoader classLoader, RedisManager redisManager) {
		super(RedisEvent.class, classLoader, redisManager);
	}

}
