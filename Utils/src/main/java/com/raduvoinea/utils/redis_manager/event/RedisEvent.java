package com.raduvoinea.utils.redis_manager.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RedisEvent extends RedisRequest<Void> {

	public RedisEvent(String className, long id, String originator, String target) {
		super(className, id, originator, target);
	}

	public RedisEvent(String target) {
		super(target);
	}

	@Override
	public boolean canRespond() {
		return false;
	}
}
