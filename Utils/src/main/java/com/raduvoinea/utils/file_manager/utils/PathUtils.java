package com.raduvoinea.utils.file_manager.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class PathUtils {

	private final static Pattern snakeCasePattern = Pattern.compile("([A-Z])");

	public static @NotNull String toSnakeCase(@NotNull String string) {
		String output = snakeCasePattern.matcher(string).replaceAll("_$1").toLowerCase();

		if (output.startsWith("_")) {
			output = output.substring(1);
		}

		return output;
	}

}
