package gg.mmorealms.module.core.backend.common.exceptions;

public class AccessedOfflineUserException extends RuntimeException {

	public AccessedOfflineUserException() {
		super("Attempted to access an offline user.");
	}
}
