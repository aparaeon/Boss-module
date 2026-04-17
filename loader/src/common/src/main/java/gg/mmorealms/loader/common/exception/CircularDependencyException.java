package gg.mmorealms.loader.common.exception;

import java.util.List;

public class CircularDependencyException extends Exception {
	public CircularDependencyException(List<String> metadataList) {
		super("Found circular dependency in modules: " + metadataList);
	}
}
