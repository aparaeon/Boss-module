package com.raduvoinea.utils.dependency_injection.exception;

public class InjectionException extends Exception {
	public InjectionException(String message) {
		super(message);
	}

	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuntimeException toRuntimeException() {
		return new RuntimeException(this);
	}
}
