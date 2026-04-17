package com.raduvoinea.utils.redis_manager.dto;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import redis.clients.jedis.params.SetParams;

public interface LockableResource {

	String getRedisLockID();

	RedisManager getRedisManager();

	Time getLockTime();

	default boolean setLock() {
		return getRedisManager().executeOnJedisAndGet(jedis -> {
			if (jedis.exists(getRedisLockID())) {
				return false;
			}

			jedis.set(getRedisLockID(), String.valueOf(System.currentTimeMillis()),
					SetParams.setParams()
							.ex(getLockTime().toSeconds())
			);
			return true;
		});
	}

	default void removeLock() {
		getRedisManager().executeOnJedisAndForget(jedis -> jedis.del(getRedisLockID()));
	}

	default void withLock(Lambda executor) {
		withLock(executor, () -> {
		});
	}

	default void withLock(Lambda executor, Lambda failExecutor) {
		if (!setLock()) {
			failExecutor.run();
			return;
		}

		executor.run();

		removeLock();
	}
}
