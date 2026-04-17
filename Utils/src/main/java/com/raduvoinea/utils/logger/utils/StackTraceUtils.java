package com.raduvoinea.utils.logger.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StackTraceUtils {

	public static String toString(Throwable throwable) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream);

		throwable.printStackTrace(printStream);

		return outputStream.toString();
	}

	public static String toString(StackTraceElement[] stackTrace) {
		StringBuilder builder = new StringBuilder();

		for (StackTraceElement element : stackTrace) {
			builder.append(element.toString()).append('\n');
		}

		return builder.toString();
	}
}
