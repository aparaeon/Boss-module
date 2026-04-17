package gg.mmorealms.loader.common.dto.event.network;


import com.raduvoinea.utils.redis_manager.event.RedisBroadcast;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import gg.mmorealms.loader.common.CommonLoader;

public abstract class NetworkBroadcast extends RedisBroadcast {

	public NetworkBroadcast() {
		super();
	}

	@Override
	public RedisManager getRedisManager() {
		return CommonLoader.instance().getRedisManager();
	}

}
