package com.raduvoinea.utils.event_manager.dto;

import com.raduvoinea.utils.event_manager.EventManager;

import java.util.concurrent.CompletableFuture;

public interface IEvent<Result> {

	boolean DEFAULT_SUPRESS_EXCEPTIONS = false;
	long DEFAULT_TIMEOUT_MILLISECONDS = 5 * 1000;

	default Result fireSync(boolean suppressExceptions) {
		return this.getEventManager().fireSync(this, suppressExceptions);
	}

	default Result fireSync() {
		return this.fireSync(DEFAULT_SUPRESS_EXCEPTIONS);
	}

	default CompletableFuture<Result> fireAsync(boolean suppressExceptions, long timeoutMilliseconds) {
		return this.getEventManager().fireAsync(this, suppressExceptions, timeoutMilliseconds);
	}

	default CompletableFuture<Result> fireAsync(boolean suppressExceptions) {
		return this.fireAsync(suppressExceptions, DEFAULT_TIMEOUT_MILLISECONDS);
	}

	default CompletableFuture<Result> fireAsync(long timeoutMilliseconds) {
		return this.fireAsync(DEFAULT_SUPRESS_EXCEPTIONS, timeoutMilliseconds);
	}

	default CompletableFuture<Result> fireAsync() {
		return this.fireAsync(DEFAULT_SUPRESS_EXCEPTIONS, DEFAULT_TIMEOUT_MILLISECONDS);
	}

	default boolean isCancelled() {
		return false;
	}

	EventManager getEventManager();

	Result getResult();

}
