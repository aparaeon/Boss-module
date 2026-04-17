package gg.mmorealms.loader.common.dto.event.network;

import com.raduvoinea.utils.redis_manager.event.RedisEvent;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import gg.mmorealms.loader.common.CommonLoader;

public abstract class NetworkEvent extends RedisEvent {

	public NetworkEvent(String redisID) {
		super(getRealRedisTarget(redisID));
	}

	public NetworkEvent() {
		this(CommonLoader.getProxyID());
	}

	private static String getRealRedisTarget(String redisID) {
		if (redisID == null || redisID.isEmpty()) {
			return CommonLoader.instance().getServerID();
		}

		return redisID;
	}

	@Override
	public RedisManager getRedisManager() {
		return CommonLoader.instance().getRedisManager();
	}
}
