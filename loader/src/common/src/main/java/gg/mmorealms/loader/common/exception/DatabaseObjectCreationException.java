package gg.mmorealms.loader.common.exception;

import gg.mmorealms.loader.common.CommonLoader;
import lombok.Getter;

@Getter
public class DatabaseObjectCreationException extends RuntimeException {

	private final Class<?> objectClass;
	private final Object identifier;

	public DatabaseObjectCreationException(Class<?> objectClass, Object identifier) {
		super("Failed to create object of class " + objectClass.getName() + " with identifier " + CommonLoader.instance().toJson(identifier) + "!)");
		this.objectClass = objectClass;
		this.identifier = identifier;
	}

}
