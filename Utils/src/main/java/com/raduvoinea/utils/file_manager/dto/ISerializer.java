package com.raduvoinea.utils.file_manager.dto;

public interface ISerializer {

	String serialize(Object object);

	<T> T deserialize(String json, Class<T> type);

}
