package com.raduvoinea.utils.file_manager.dto.serializable;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class SerializableList<Value> extends ArrayList<Value> {

	private final Class<Value> valueClass;

	public SerializableList(Class<Value> valueClass, Collection<Value> list) {
		super(list);
		this.valueClass = valueClass;
	}

	@SuppressWarnings("unused")
	public SerializableList(Class<Value> clazz) {
		this(clazz, new ArrayList<>());
	}
}
