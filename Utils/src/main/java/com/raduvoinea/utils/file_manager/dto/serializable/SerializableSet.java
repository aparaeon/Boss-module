package com.raduvoinea.utils.file_manager.dto.serializable;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Getter
public class SerializableSet<Value> extends HashSet<Value> {

	private final Class<Value> valueClass;

	public SerializableSet(Class<Value> valueClass, Collection<Value> list) {
		super(list);
		this.valueClass = valueClass;
	}

	@SuppressWarnings("unused")
	public SerializableSet(Class<Value> clazz) {
		this(clazz, new ArrayList<>());
	}
}
