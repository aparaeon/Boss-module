package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.logger.ChildLoggerInstance;
import com.raduvoinea.utils.logger.LoggerInstance;
import org.jetbrains.annotations.NotNull;

public class Slf4jLogHandler extends ChildLoggerInstance {

	private final org.slf4j.Logger parent;

	public Slf4jLogHandler(org.slf4j.Logger parent) {
		super(LoggerInstance.DEFAULT);
		this.parent = parent;
	}

	@Override
	protected void handleInfo(@NotNull String log) {
		parent.info(log);
	}

	@Override
	protected void handleError(@NotNull String log) {
		parent.error(log);
	}

	@Override
	protected void handleWarn(@NotNull String log) {
		parent.warn(log);
	}

	@Override
	protected void handleDebug(@NotNull String log) {
		parent.debug(log);
	}
}
