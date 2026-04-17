package gg.mmorealms.loader.common.dto.event.network;

import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import org.jetbrains.annotations.NotNull;

public abstract class NetworkRequest<T> extends RedisRequest<T> {

	public NetworkRequest(@NotNull String redisID) {
		super(redisID);
	}

	public NetworkRequest() {
		this(CommonLoader.getProxyID());
	}

	@Override
	public RedisManager getRedisManager() {
		return CommonLoader.instance().getRedisManager();
	}

}
