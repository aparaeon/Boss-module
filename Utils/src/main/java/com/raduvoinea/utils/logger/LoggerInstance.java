package com.raduvoinea.utils.logger;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.logger.dto.ConsoleColor;
import com.raduvoinea.utils.logger.dto.Level;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

@Getter
@Setter
public abstract class LoggerInstance {

	public static final LoggerInstance DEFAULT;
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	private Level logLevel;
	private boolean printSourceClass;

	public LoggerInstance() {
		this(Level.TRACE, true);
	}

	public LoggerInstance(Level logLevel, boolean printSourceClass) {
		this.logLevel = logLevel;
		this.printSourceClass = printSourceClass;
	}

	static {
		DEFAULT = new LoggerInstance() {
			private final PrintStream outPrintStream = System.out;
			private final PrintStream errPrintStream = System.err;

			@Override
			protected void handleInfo(@NotNull String log) {
				this.outPrintStream.println(log);
				this.outPrintStream.flush();
			}

			@Override
			protected void handleError(@NotNull String log) {
				this.errPrintStream.println(log);
				this.errPrintStream.flush();
			}

			@Override
			protected void handleWarn(@NotNull String log) {
				this.outPrintStream.println(log);
				this.outPrintStream.flush();
			}

			@Override
			protected void handleDebug(@NotNull String log) {
				this.outPrintStream.println(log);
				this.outPrintStream.flush();
			}

			@Override
			protected @Nullable String parsePackage(String packageName) {
				return null;
			}
		};
	}

	public void debug(@Nullable Object object) {
		this.debug(object, ConsoleColor.BRIGHT_BLACK);
	}

	public void debug(@Nullable Object object, ConsoleColor color) {
		this.log(Level.DEBUG, object, color, this::handleDebug);
	}

	public void log(@Nullable Object object) {
		this.info(object);
	}

	public void info(@Nullable Object object) {
		this.log(Level.INFO, object, ConsoleColor.RESET, this::handleInfo);
	}

	public void good(@Nullable Object object) {
		this.log(Level.INFO, object, ConsoleColor.DARK_GREEN, this::handleInfo);
	}

	public void warn(@Nullable Object object) {
		this.log(Level.WARN, object, ConsoleColor.DARK_YELLOW, this::handleWarn);
	}

	public void error(@Nullable Object object) {
		this.log(Level.ERROR, object, ConsoleColor.DARK_RED, this::handleError);
	}

	private void log(@NotNull Level level, @Nullable Object object, @NotNull ConsoleColor color, ArgLambda<String> logger) {
		if (level.getLevel() < logLevel.getLevel()) {
			return;
		}

		String id = null;

		if (printSourceClass) {
			Class<?> caller = getCallerClass();
			id = this.parsePackage(caller.getPackageName());
			if (id == null || id.isEmpty()) {
				id = caller.getSimpleName();
			}

			id = "[" + id + "] ";
		}

		String log = switch (object) {
			case null -> "null";
			case MessageBuilder messageBuilder -> messageBuilder.parse();
			case Throwable throwable -> StackTraceUtils.toString(throwable);
			case StackTraceElement[] stackTraceElements -> StackTraceUtils.toString(stackTraceElements);
			default -> object.toString();
		};

		String finalLog = "";
		finalLog += color;
		if (id != null) {
			finalLog += id;
		}
		finalLog += String.join("\n" + color, log.split("\n"));
		finalLog += ConsoleColor.RESET;

		finalLog = finalProcessor(finalLog);

		if (finalLog == null) {
			return;
		}

		logger.run(finalLog);
	}

	protected abstract void handleInfo(@NotNull String log);

	protected abstract void handleError(@NotNull String log);

	protected abstract void handleWarn(@NotNull String log);

	protected abstract void handleDebug(@NotNull String log);

	protected @Nullable String finalProcessor(String log) {
		return log;
	}

	protected abstract @Nullable String parsePackage(String packageName);

	private static @NotNull Class<?> getCallerClass() {
		Class<?> clazz = STACK_WALKER.walk(stack -> stack.map(StackWalker.StackFrame::getDeclaringClass)
			.filter(c -> !Logger.class.isAssignableFrom(c) && !LoggerInstance.class.isAssignableFrom(c))
			.findFirst()
		).orElse(null);

		if (clazz == null) {
			System.out.println("<!> Failed to get caller class <!>");
			clazz = Logger.class;
		}

		return clazz;
	}

}
