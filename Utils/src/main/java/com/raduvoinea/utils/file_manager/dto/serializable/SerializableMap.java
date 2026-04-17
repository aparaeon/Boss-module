package com.raduvoinea.utils.file_manager.dto.serializable;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SerializableMap<Key, Value> extends HashMap<Key, Value> {

	private final Class<Key> keyClass;
	private final Class<Value> valueClass;

	public SerializableMap(Class<Key> keyClass, Class<Value> valueClass, Map<Key, Value> map) {
		this.keyClass = keyClass;
		this.valueClass = valueClass;
		putAll(map);
	}

	@SuppressWarnings("unused")
	public SerializableMap(Class<Key> keyClass, Class<Value> valueClass) {
		this(keyClass, valueClass, new HashMap<>());
	}

}
