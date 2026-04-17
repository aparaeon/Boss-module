package com.raduvoinea.utils.file_manager.dto.serializable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SerializableObject<Object> {

	private Class<Object> objectClass;
	private Object object;

	public SerializableObject(Object object) {
		this.objectClass = object == null ? null : (Class<Object>) object.getClass();
		this.object = object;
	}
}