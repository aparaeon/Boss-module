package com.raduvoinea.logger.dto;

import com.raduvoinea.utils.logger.ChildLoggerInstance;
import lombok.Getter;

@Getter
public class TestChildLogger extends ChildLoggerInstance {

	public TestChildLogger() {
		this(new TestLogger());
	}

	public TestChildLogger(TestLogger testLogger) {
		super(testLogger);
	}

}
