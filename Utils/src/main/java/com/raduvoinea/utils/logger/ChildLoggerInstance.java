package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.logger.dto.Level;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ChildLoggerInstance extends LoggerInstance {

	private final @Nullable LoggerInstance parent;

	public ChildLoggerInstance(@Nullable LoggerInstance parent) {
		//noinspection SimplifiableConditionalExpression
		super(
			parent == null ? Level.TRACE : parent.getLogLevel(),
			parent == null ? true : parent.isPrintSourceClass()
		);
		this.parent = parent;
	}

	protected void handleInfo(@NotNull String log) {
		if (parent != null) {
			this.parent.handleInfo(log);
		}
	}

	protected void handleError(@NotNull String log) {
		if (parent != null) {
			this.parent.handleError(log);
		}
	}

	protected void handleWarn(@NotNull String log) {
		if (parent != null) {
			this.parent.handleWarn(log);
		}
	}

	protected void handleDebug(@NotNull String log) {
		if (parent != null) {
			this.parent.handleDebug(log);
		}
	}

	protected String finalProcessor(String log) {
		if (parent != null) {
			return this.parent.finalProcessor(log);
		}
		return log;
	}

	protected @Nullable String parsePackage(String packageName) {
		if (parent != null) {
			return this.parent.parsePackage(packageName);
		}

		return null;
	}

	@Override
	public void setPrintSourceClass(boolean printSourceClass) {
		super.setPrintSourceClass(printSourceClass);
		if (parent != null) {

			this.parent.setPrintSourceClass(printSourceClass);
		}
	}

	@Override
	public void setLogLevel(Level logLevel) {
		super.setLogLevel(logLevel);
		if (parent != null) {
			this.parent.setLogLevel(logLevel);
		}
	}
}
