package com.raduvoinea.utils.redis_manager.manager;

import com.google.gson.Gson;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.redis_manager.dto.RedisConfig;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

@Getter
public class DebugRedisManager extends RedisManager {

	private final List<String> channels;

	public DebugRedisManager(Holder<Gson> gsonProvider, RedisConfig redisConfig, ClassLoader classLoader, Holder<EventManager> eventManagerHolder, boolean debug, boolean localOnly, List<String> channels) {
		super(gsonProvider, redisConfig, classLoader, eventManagerHolder, debug, localOnly);
		this.channels = channels;
	}

	@Override
	protected void subscribe() {
		subscriberJedisPubSub = new JedisPubSub() {

			public void onMessage(String channel, final String command) {
				try {
					onMessageReceive(channel, command);
				} catch (Throwable throwable) {
					Logger.error("There was an error while receiving a message from Redis.");
					Logger.error(throwable);
				}
			}

			public void onMessageReceive(String channel, final String event) {
				getDebugger().receive(channel, event);
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				getDebugger().subscribed(channel);
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				getDebugger().unsubscribed(channel);
			}
		};

		startRedisThread();
	}

	@Override
	protected String[] getChannels() {
		return channels.toArray(new String[0]);
	}
}