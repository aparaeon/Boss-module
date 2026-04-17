package com.raduvoinea.utils.generic.exception;

import java.util.List;

public class CircularDependencyException extends Exception {
	public CircularDependencyException(List<?> cycle) {
		super("Found circular dependency in modules: " + cycle);
	}
}
