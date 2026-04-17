package com.raduvoinea.utils.redis_manager.manager;

import com.google.gson.Gson;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.redis_manager.dto.RedisConfig;
import com.raduvoinea.utils.redis_manager.event.RedisBroadcast;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.event.impl.ResponseEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RedisManager {

	private static @Getter RedisManager instance;

	private final @Getter ConcurrentHashMap<Long, CompletableFuture<?>> awaitingResponses;
	private final @Getter RedisDebugger debugger;
	private final @Getter RedisConfig redisConfig;
	private final @Getter ClassLoader classLoader;
	private final @Getter boolean localOnly;
	private final @Getter Holder<Gson> gsonHolder;
	private final @Getter Holder<EventManager> eventManagerHolder;
	protected @Getter JedisPubSub subscriberJedisPubSub;
	private @Getter Thread redisTread;
	private @Getter long id;
	private JedisPool jedisPool;

	@SuppressWarnings("unused")
	public <T> T executeOnJedisAndGet(ReturnArgLambda<T, Jedis> executor) {
		return executeOnJedisAndGet(executor, error -> {
		});
	}

	public <T> T executeOnJedisAndGet(ReturnArgLambda<T, Jedis> executor, ArgLambda<Exception> failExecutor) {
		try (Jedis jedis = jedisPool.getResource()) {
			return executor.run(jedis);
		} catch (Exception error) {
			Logger.error(error);
			failExecutor.run(error);
			return null;
		}
	}

	public void executeOnJedisAndForget(ArgLambda<Jedis> executor) {
		executeOnJedisAndForget(executor, exception -> {
		});
	}

	public void executeOnJedisAndForget(ArgLambda<Jedis> executor, ArgLambda<Exception> failExecutor) {
		if (localOnly) {
			Logger.warn("Attempted to execute Redis command while in local only mode. Command was not executed.");
			return;
		}

		try (Jedis jedis = jedisPool.getResource()) {
			executor.run(jedis);
		} catch (Exception error) {
			Logger.error(error);
			failExecutor.run(error);
		}
	}

	public RedisManager(Holder<Gson> gsonProvider, RedisConfig redisConfig, ClassLoader classLoader, Holder<EventManager> eventManagerHolder) {
		this(gsonProvider, redisConfig, classLoader, eventManagerHolder, false);
	}

	public RedisManager(Holder<Gson> gsonProvider, RedisConfig redisConfig, ClassLoader classLoader, Holder<EventManager> eventManagerHolder, boolean debug) {
		this(gsonProvider, redisConfig, classLoader, eventManagerHolder, debug, false);
	}

	public RedisManager(Holder<Gson> gsonHolder, RedisConfig redisConfig, ClassLoader classLoader, Holder<EventManager> eventManagerHolder, boolean debug, boolean localOnly) {
		instance = this;

		this.gsonHolder = gsonHolder;
		this.redisConfig = redisConfig;
		this.localOnly = localOnly;
		this.classLoader = classLoader;

		this.debugger = new RedisDebugger(debug);
		this.eventManagerHolder = eventManagerHolder;
		this.awaitingResponses = new ConcurrentHashMap<>();

		if (!localOnly) {
			connectJedis();
			subscribe();
		}
	}

	@SuppressWarnings("unused")
	public void setDebug(boolean debug) {
		debugger.setEnabled(debug);
	}


	private void connectJedis() {
		if (jedisPool != null) {
			jedisPool.destroy();
		}

		JedisPoolConfig jedisConfig = new JedisPoolConfig();
		jedisConfig.setMaxTotal(16);

		jedisPool = new JedisPool(
			jedisConfig,
			redisConfig.getHost(),
			redisConfig.getPort(),
			redisConfig.getTimeout(),
			redisConfig.getPassword()
		);
	}


	protected void subscribe() {
		RedisManager _this = this;
		subscriberJedisPubSub = new JedisPubSub() {

			public void onMessage(String channel, final String command) {
				try {
					onMessageReceive(channel, command);
				} catch (Throwable throwable) {
					Logger.error("There was an error while receiving a message from Redis.");
					Logger.error(throwable);
				}
			}

			public void onMessageReceive(String channel, final String eventJson) {
				if (eventJson.isEmpty()) {
					Logger.warn("Received empty RedisEvent");
					return;
				}

				RedisRequest<?> event = gsonHolder.value().fromJson(eventJson, RedisRequest.class);

				if (event == null) {
					Logger.warn("Received invalid RedisEvent: " + eventJson);
					return;
				}

				event = switch (event) {
					case RedisBroadcast broadcast -> {
						if (redisConfig.getRedisID().equals(broadcast.getOriginator())) {
							// [!] Do not self file broadcast events // TODO Add to java docs of RedisBroadcast
							yield null;
						}
						yield broadcast;
					}
					case ResponseEvent responseEvent -> {
						debugger.receiveResponse(channel, eventJson);
						//noinspection rawtypes
						CompletableFuture response = awaitingResponses.remove(responseEvent.getId());
						if (response == null) {
							Logger.debug("Received response for non existent event: " + responseEvent.getId());
							yield null;
						}

						//noinspection unchecked
						response.complete(responseEvent.deserialize());

						yield null;
					}
					default -> event;
				};

				if (event == null) {
					return;
				}

				RedisRequest<?> finalEvent = event;
				ScheduleUtils.runTaskAsync(() -> {
					debugger.receive(channel, eventJson);

					Object result = finalEvent.fireSync();

					if (finalEvent.canRespond()) {
						new ResponseEvent(_this, finalEvent, result).send();
					}
				});
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				debugger.subscribed(channel);
			}

			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				debugger.unsubscribed(channel);
			}
		};


		startRedisThread();
	}

	protected void startRedisThread() {
		if (redisTread != null) {
			redisTread.interrupt();
		}

		Logger.log(new MessageBuilder("[RedisManager] Starting Redis {host}:{port} thread for channels {channels}")
			.parse("host", redisConfig.getHost())
			.parse("port", redisConfig.getPort())
			.parse("channels", Arrays.toString(getChannels()))
		);

		redisTread = new Thread(() ->
			executeOnJedisAndForget(
				jedis -> jedis.subscribe(subscriberJedisPubSub, getChannels()),
				exception -> {
					Logger.error("Lost connection to redis server. Retrying in 3 seconds...");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException ignored) {
					}

					Logger.good("Reconnecting to redis server.");
					startRedisThread();
				})
		);
		redisTread.start();
	}

	protected String[] getChannels() {
		Set<String> result = new HashSet<>();

		result.add(redisConfig.getChannel() + "#" + redisConfig.getRedisID());
		result.add(redisConfig.getChannel() + "#*");

		result.addAll(Arrays.asList(redisConfig.getAdditionalListenChannels().split(",")));

		return result.toArray(new String[0]);
	}

	public <T> CompletableFuture<T> send(@NotNull RedisRequest<T> event) {
		event.setOriginator(redisConfig.getRedisID());

		if (event instanceof ResponseEvent responseEvent) {
			sendResponse(responseEvent);
			return CompletableFuture.completedFuture(null);
		}

		if (event.getTarget() == null) {
			return CompletableFuture.completedFuture(null);
		}

		if (event.getTarget().equals(event.getOriginator())) {
			debugger.send("LOCAL", gsonHolder.value().toJson(event));
			return event.fireAsync();
		}

		id++;
		event.setId(id);
		CompletableFuture<T> future;

		if (event.canRespond()) {
			future = new CompletableFuture<>();
			awaitingResponses.put(event.getId(), future);
		} else {
			future = CompletableFuture.completedFuture(null);
		}

		debugger.send(event.getPublishChannel(), gsonHolder.value().toJson(event));

		executeOnJedisAndForget(jedis ->
			jedis.publish(event.getPublishChannel(), gsonHolder.value().toJson(event))
		);

		return future.orTimeout(2, TimeUnit.MINUTES); // TODO Config
	}

	public void sendResponse(ResponseEvent responseEvent) {
		debugger.sendResponse(responseEvent.getPublishChannel(), gsonHolder.value().toJson(responseEvent));

		executeOnJedisAndForget(jedis ->
			jedis.publish(responseEvent.getPublishChannel(), gsonHolder.value().toJson(responseEvent))
		);
	}
}