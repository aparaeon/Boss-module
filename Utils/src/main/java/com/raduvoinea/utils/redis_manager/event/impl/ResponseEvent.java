package com.raduvoinea.utils.redis_manager.event.impl;

import com.google.gson.reflect.TypeToken;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseEvent extends RedisRequest<Object> {

	private static final String EMPTY_LIST = "EMPTY_LIST";

	private final String response;
	private final String responseClassName;
	private String additionalData;

	private transient @Setter RedisManager redisManager;

	public ResponseEvent(RedisManager redisManager, RedisRequest<?> command, Object response) {
		super(command.getOriginator());
		this.setId(command.getId());
		this.redisManager = redisManager;

		if (response == null) {
			this.response = "";
			this.responseClassName = "";
			return;
		}

		this.response = redisManager.getGsonHolder().value().toJson(response);
		this.responseClassName = response.getClass().getName();

		if (response.getClass().isAssignableFrom(List.class)) {
			ArrayList<?> list = (ArrayList<?>) response;

			if (list.isEmpty()) {
				additionalData = EMPTY_LIST;
				return;
			}

			additionalData = list.getFirst().getClass().getName();
		}
	}

	@SneakyThrows(value = {ClassNotFoundException.class})
	public Object deserialize() {
		if (responseClassName == null || responseClassName.isEmpty()) {
			return null;
		}

		Class<?> responseClass = redisManager.getClassLoader().loadClass(responseClassName);

		if (responseClass.isAssignableFrom(List.class)) {
			if (additionalData.equals(EMPTY_LIST)) {
				return new ArrayList<>();
			}

			Class<?> aditionalClass = redisManager.getClassLoader().loadClass(additionalData);

			return redisManager.getGsonHolder().value().fromJson(response, TypeToken.getParameterized(List.class, aditionalClass));
		}

		return redisManager.getGsonHolder().value().fromJson(response, responseClass);
	}

	@SneakyThrows(value = {ClassNotFoundException.class})
	public Class<?> getResponseClass() {
		return redisManager.getClassLoader().loadClass(responseClassName);
	}

	@Override
	public RedisManager getRedisManager() {
		return redisManager;
	}
}
