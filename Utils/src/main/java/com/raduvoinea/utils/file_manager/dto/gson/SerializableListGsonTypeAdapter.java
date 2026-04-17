package com.raduvoinea.utils.file_manager.dto.gson;

import com.google.gson.*;
import com.raduvoinea.utils.file_manager.dto.GsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.serializable.SerializableList;
import com.raduvoinea.utils.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("rawtypes")
public class SerializableListGsonTypeAdapter extends GsonTypeAdapter<SerializableList> {

	private static final String CLASS_NAME_FIELD = "class_name";
	private static final String VALUES = "values";

	public SerializableListGsonTypeAdapter(ClassLoader classLoader) {
		super(classLoader, SerializableList.class);
	}

	@Override
	public SerializableList deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		String className = json.getAsJsonObject().get(CLASS_NAME_FIELD).getAsString();

		try {
			Class clazz = classLoader.loadClass(className);

			JsonArray data = json.getAsJsonObject().get(VALUES).getAsJsonArray();
			//noinspection unchecked
			List output = new ArrayList(data.asList().stream().map(element -> context.deserialize(element, clazz)).toList());

			//noinspection unchecked
			return new SerializableList(clazz, output);
		} catch (ClassNotFoundException error) {
			Logger.error(error);
			return null;
		}
	}

	@Override
	public JsonElement serialize(SerializableList list, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty(CLASS_NAME_FIELD, list.getValueClass().getName());

		JsonArray array = new JsonArray();
		//noinspection unchecked
		list.forEach((element) -> array.add(context.serialize(element)));

		object.add(VALUES, array);

		return object;
	}
}
