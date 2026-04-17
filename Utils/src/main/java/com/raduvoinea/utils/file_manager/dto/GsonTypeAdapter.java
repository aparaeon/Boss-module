package com.raduvoinea.utils.file_manager.dto;

import com.google.gson.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class GsonTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

	protected final ClassLoader classLoader;
	private final @Getter Class<T> serializedClass;

	public GsonTypeAdapter(ClassLoader classLoader, Class<T> serializedClass) {
		this.serializedClass = serializedClass;
		this.classLoader = classLoader;
	}

	public void register(@NotNull GsonBuilder builder) {
		builder.registerTypeAdapter(this.serializedClass, this);
	}

	@Override
	public abstract JsonElement serialize(T object, Type type, JsonSerializationContext context);

	@Override
	public abstract T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException;

	protected <ListType> List<ListType> deserializeList(JsonArray jsonArray, Class<ListType> clazz, JsonDeserializationContext context) {
		List<ListType> list = new ArrayList<>();

		for (JsonElement jsonElement : jsonArray) {
			list.add(context.deserialize(jsonElement, clazz));
		}

		return list;
	}
}
