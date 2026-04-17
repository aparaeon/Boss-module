package com.raduvoinea.utils.file_manager.dto.gson;

import com.google.gson.*;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableMap;
import com.raduvoinea.utils.logger.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;


@SuppressWarnings("rawtypes")
public class SerializableMapGsonTypeAdapter extends GsonTypeAdapter<SerializableMap> {

	private static final String KEY_CLASS_NAME = "key_class_name";
	private static final String VALUE_CLASS_NAME = "value_class_name";
	private static final String KEYS = "keys";
	private static final String VALUES = "values";

	public SerializableMapGsonTypeAdapter(ClassLoader classLoader) {
		super(classLoader, SerializableMap.class);
	}

	@Override
	public SerializableMap deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		String keyClassName = json.getAsJsonObject().get(KEY_CLASS_NAME).getAsString();
		String valueClassName = json.getAsJsonObject().get(VALUE_CLASS_NAME).getAsString();

		try {
			Class keyClass = classLoader.loadClass(keyClassName);
			Class valueClass = classLoader.loadClass(valueClassName);

			JsonArray keys = json.getAsJsonObject().get(KEYS).getAsJsonArray();
			JsonArray values = json.getAsJsonObject().get(VALUES).getAsJsonArray();

			HashMap output = new HashMap<>();

			for (int i = 0; i < keys.size(); i++) {
				//noinspection unchecked
				output.put(context.deserialize(keys.get(i), keyClass), context.deserialize(values.get(i), valueClass));
			}

			//noinspection unchecked
			return new SerializableMap(keyClass, valueClass, output);

		} catch (ClassNotFoundException error) {
			Logger.error(error);
			return null;
		}
	}

	@Override
	public JsonElement serialize(SerializableMap list, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty(KEY_CLASS_NAME, list.getKeyClass().getName());
		object.addProperty(VALUE_CLASS_NAME, list.getValueClass().getName());

		JsonArray keyArray = new JsonArray();
		JsonArray valueArray = new JsonArray();

		//noinspection unchecked
		list.forEach((key, value) -> {
			keyArray.add(context.serialize(key));
			valueArray.add(context.serialize(value));
		});

		object.add(KEYS, keyArray);
		object.add(VALUES, valueArray);

		return object;
	}
}
