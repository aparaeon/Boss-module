package gg.mmorealms.loader.common.exception;

public class SyncedRequestException extends RuntimeException {
	public SyncedRequestException(String message) {
		super(message);
	}

	public SyncedRequestException(Throwable cause) {
		super(cause);
	}

}
