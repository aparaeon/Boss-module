package com.raduvoinea.utils.message_builder.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.lang.reflect.Type;

public class MessageBuilderTypeAdapter extends GsonTypeAdapter<MessageBuilder> {

	public MessageBuilderTypeAdapter(ClassLoader classLoader) {
		super(classLoader, MessageBuilder.class);
	}

	@Override
	public MessageBuilder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
		return new MessageBuilder(jsonElement.getAsString());
	}

	@Override
	public JsonElement serialize(MessageBuilder src, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(src.getBase());
	}

}
