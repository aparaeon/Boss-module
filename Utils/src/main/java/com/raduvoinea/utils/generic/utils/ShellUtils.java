package com.raduvoinea.utils.generic.utils;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtils {

	public static @NotNull String executeAndCapture(@NotNull String command) throws IOException, InterruptedException {
		StringBuilder output = new StringBuilder();
		ShellUtils.executeGeneric(command, output::append);
		return output.toString();
	}

	public static void execute(String command) throws IOException, InterruptedException {
		ShellUtils.executeGeneric(command, System.out::println);
	}

	public static void executeGeneric(String command, ArgLambda<String> lineExecutor) throws IOException, InterruptedException {
		Process process = new ProcessBuilder(command.split("\\s+"))
				.redirectErrorStream(true)
				.start();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lineExecutor.run(line);
			}
		}

		int exitCode = process.waitFor();
		if (exitCode != 0) {
			System.err.println("Command exited with code: " + exitCode);
		}
	}

}
