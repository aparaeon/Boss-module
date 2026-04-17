package com.raduvoinea.utils.redis_manager.event;

public abstract class RedisBroadcast extends RedisEvent {

	public RedisBroadcast(String className, long id, String originator, String target) {
		super(className, id, originator, target);
	}

	public RedisBroadcast() {
		super("*");
	}

}
