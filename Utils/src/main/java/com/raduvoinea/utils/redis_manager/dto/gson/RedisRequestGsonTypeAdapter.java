package com.raduvoinea.utils.redis_manager.dto.gson;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;

@SuppressWarnings("rawtypes")
public class RedisRequestGsonTypeAdapter extends GenericRedisRequestGsonTypeAdapter<RedisRequest> {

	public RedisRequestGsonTypeAdapter(ClassLoader classLoader, RedisManager redisManager) {
		super(RedisRequest.class, classLoader, redisManager);
	}

}
