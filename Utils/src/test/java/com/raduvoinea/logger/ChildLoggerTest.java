package com.raduvoinea.logger;

import com.raduvoinea.logger.dto.TestChildLogger;
import com.raduvoinea.logger.dto.TestLogger;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.dto.Level;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChildLoggerTest {

	@BeforeEach
	public void beforeEach() {
		Logger.reset();
		Logger.setInstance(new TestChildLogger());
	}

	private @NotNull TestChildLogger getInstance() {
		if (Logger.getInstance() instanceof TestChildLogger testChildLogger) {
			return testChildLogger;
		}

		fail();
		return null;
	}

	private @NotNull TestLogger getParent(){
		TestChildLogger testChildLogger = getInstance();

		if(testChildLogger.getParent() instanceof TestLogger testLogger) {
			return testLogger;
		}

		fail();
		return null;
	}

	@Test
	public void testDebugLogger() {
		Logger.getInstance().setLogLevel(Level.DEBUG);

		Logger.debug("testDebugLogger#debug");
		Logger.log("testDebugLogger#log");
		Logger.good("testDebugLogger#good");
		Logger.warn("testDebugLogger#warn");
		Logger.error("testDebugLogger#error");

		assertEquals(5, this.getParent().getBuffer().size());
	}

	@Test
	public void testInfoLogger() {
		Logger.getInstance().setLogLevel(Level.INFO);

		Logger.debug("testInfoLogger#debug");
		Logger.log("testInfoLogger#log");
		Logger.good("testInfoLogger#good");
		Logger.warn("testInfoLogger#warn");
		Logger.error("testInfoLogger#error");

		assertEquals(4, this.getParent().getBuffer().size());
	}

	@Test
	public void testWarnLogger() {
		Logger.getInstance().setLogLevel(Level.WARN);

		Logger.debug("testWarnLogger#debug");
		Logger.log("testWarnLogger#log");
		Logger.good("testWarnLogger#good");
		Logger.warn("testWarnLogger#warn");
		Logger.error("testWarnLogger#error");

		assertEquals(2, this.getParent().getBuffer().size());
	}

	@Test
	public void testErrorLogger() {
		Logger.getInstance().setLogLevel(Level.ERROR);

		Logger.debug("testErrorLogger#debug");
		Logger.log("testErrorLogger#log");
		Logger.good("testErrorLogger#good");
		Logger.warn("testErrorLogger#warn");
		Logger.error("testErrorLogger#error");

		assertEquals(1, this.getParent().getBuffer().size());
	}

	@Test
	public void testFormatClass() {
		Logger.log("testErrorLogger#log");

		assertEquals(1, this.getParent().getBuffer().size());
		assertEquals(ConsoleColor.RESET + "[ChildLoggerTest] testErrorLogger#log" + ConsoleColor.RESET, this.getParent().getBuffer().getFirst());
	}

	@Test
	public void testFormatPackage() {
		Logger.setInstance(new TestChildLogger(new TestLogger(true)));
		Logger.log("testErrorLogger#log");

		assertEquals(1, this.getParent().getBuffer().size());
		assertEquals(ConsoleColor.RESET + "[LoggerTestPackage] testErrorLogger#log" + ConsoleColor.RESET, this.getParent().getBuffer().getFirst());
	}

	@Test
	public void testSoutOverride() {
		{
			Logger.uninstallPrintStream();
			System.out.println("testSoutOverride");
			assertEquals(0, this.getParent().getBuffer().size());
		}

		Logger.installPrintStream();

		{
			System.out.println("testSoutOverride");
			assertEquals(1, this.getParent().getBuffer().size());
		}

		{
			Logger.setInstance(new TestChildLogger());
			System.out.println("testSoutOverride");
			assertEquals(1, this.getParent().getBuffer().size());
		}
	}

}
