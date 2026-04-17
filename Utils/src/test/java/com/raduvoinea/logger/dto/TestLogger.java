package com.raduvoinea.logger.dto;

import com.raduvoinea.utils.logger.LoggerInstance;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestLogger extends LoggerInstance {

	private final List<String> buffer;
	private final boolean parsePackage;

	public TestLogger() {
		this(false);
	}

	public TestLogger(boolean parsePackage) {
		this.buffer = new ArrayList<>();
		this.parsePackage = parsePackage;
	}

	@Override
	protected void handleInfo(@NotNull String log) {
		buffer.add(log);
		LoggerInstance.DEFAULT.info(log);
	}

	@Override
	protected void handleError(@NotNull String log) {
		buffer.add(log);
		LoggerInstance.DEFAULT.error(log);
	}

	@Override
	protected void handleWarn(@NotNull String log) {
		buffer.add(log);
		LoggerInstance.DEFAULT.warn(log);
	}

	@Override
	protected void handleDebug(@NotNull String log) {
		buffer.add(log);
		LoggerInstance.DEFAULT.debug(log);
	}

	@Override
	protected @Nullable String parsePackage(String packageName) {
		if(!parsePackage) {
			return null;
		}
		return "LoggerTestPackage";
	}
}
