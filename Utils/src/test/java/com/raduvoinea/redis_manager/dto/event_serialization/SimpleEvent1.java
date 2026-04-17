package com.raduvoinea.redis_manager.dto.event_serialization;

import com.raduvoinea.redis_manager.RedisTests;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;

@Getter
public class SimpleEvent1 extends RedisRequest<Integer> {

	private final int a;
	private final int b;

	public SimpleEvent1(int a, int b) {
		super(RedisTests.REDIS_MANAGER.getRedisConfig().getRedisID());

		this.a = a;
		this.b = b;
	}

	@Override
	public RedisManager getRedisManager() {
		return RedisTests.REDIS_MANAGER;
	}

}
