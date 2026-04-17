package com.raduvoinea.redis_manager.dto.event_serialization;

import com.raduvoinea.redis_manager.RedisTests;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;

import java.util.List;

@Getter
public class ComplexEvent1 extends RedisRequest<List<String>> {

	private final List<String> a;
	private final String b;

	public ComplexEvent1(List<String> a, String b) {
		super(RedisTests.REDIS_MANAGER.getRedisConfig().getRedisID());

		this.a = a;
		this.b = b;
	}

	@Override
	public RedisManager getRedisManager() {
		return RedisTests.REDIS_MANAGER;
	}
}