package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class LoggerOutputStream extends OutputStream {
	private final ArgLambda<String> logger;
	private final StringBuilder buffer = new StringBuilder();
	private final boolean autoFlush;

	LoggerOutputStream(ArgLambda<String> logger, boolean autoFlush) {
		this.logger = logger;
		this.autoFlush = autoFlush;
	}

	@Override
	public void write(int b) {
		if (b == '\n') {
			flush();
		} else if (b != '\r') {
			buffer.append((char) b);
		}
	}

	@Override
	public void write(byte @NotNull [] b, int off, int len) {
		for (int i = off; i < off + len; i++) {
			write(b[i]);
		}
	}

	@Override
	public void flush() {
		if (!buffer.isEmpty()) {
			logger.run(buffer.toString());
			buffer.setLength(0);
		}
		if (autoFlush) {
			try {
				super.flush();
			} catch (IOException ignored) {
			}
		}
	}
}