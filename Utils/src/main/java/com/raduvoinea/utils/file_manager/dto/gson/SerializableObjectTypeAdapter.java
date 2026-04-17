package com.raduvoinea.utils.file_manager.dto.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableObject;
import com.raduvoinea.utils.logger.Logger;

import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SerializableObjectTypeAdapter extends GsonTypeAdapter<SerializableObject> {

	private static final String CLASS_NAME = "class_name";
	private static final String DATA = "data";

	private static final Map<String, Class<?>> PRIMITIVE_TYPES = Map.of(
		"boolean", boolean.class,
		"byte", byte.class,
		"char", char.class,
		"double", double.class,
		"float", float.class,
		"int", int.class,
		"long", long.class,
		"short", short.class,
		"void", void.class
	);

	public SerializableObjectTypeAdapter(ClassLoader classLoader) {
		super(classLoader, SerializableObject.class);
	}

	private Class<?> resolveClass(String className) throws ClassNotFoundException {
		Class<?> primitive = PRIMITIVE_TYPES.get(className);
		if (primitive != null) {
			return primitive;
		}
		return classLoader.loadClass(className);
	}

	@Override
	public SerializableObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();

		JsonElement jsonData = jsonObject.get(DATA);
		JsonElement classNameElement = jsonObject.get(CLASS_NAME);

		if (classNameElement == null || classNameElement.isJsonNull()) {
			return new SerializableObject(null);
		}

		String className = classNameElement.getAsString();

		try {
			Class<?> clazz = resolveClass(className);
			Object object = context.deserialize(jsonData, clazz);

			return new SerializableObject(object);
		} catch (ClassNotFoundException exception) {
			Logger.error(exception);
			return new SerializableObject(null);
		}
	}

	@Override
	public JsonElement serialize(SerializableObject object, Type type, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty(CLASS_NAME, object.getObjectClass().getName());
		jsonObject.add(DATA, context.serialize(object.getObject()));

		return jsonObject;
	}
}