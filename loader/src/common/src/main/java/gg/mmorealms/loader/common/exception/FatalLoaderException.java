package gg.mmorealms.loader.common.exception;

public class FatalLoaderException extends RuntimeException {
	public FatalLoaderException(String message) {
		this(message, null);
	}

	public FatalLoaderException(String message, Exception cause) {
		super(message, cause);
	}
}
