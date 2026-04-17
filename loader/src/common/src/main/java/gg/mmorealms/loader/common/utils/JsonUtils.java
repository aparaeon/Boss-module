package gg.mmorealms.loader.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

	public static JsonObject toJsonObject(String data) {
		JsonElement element = JsonParser.parseString(data);
		return (JsonObject) element;
	}

}
