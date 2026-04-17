package com.raduvoinea.utils.lambda;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.logger.utils.StackTraceUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class ScheduleUtils {

	private static final Executor EXECUTOR_POOL = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
	private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newSingleThreadScheduledExecutor();

	public static @NotNull CancelableTimeTask runTaskLater(@NotNull Lambda executor, Time delay) {
		CancelableTimeTask task = new CancelableTimeTask() {
			@Override
			public void execute() {
				try {
					executor.run();
				} catch (Throwable throwable) {
					Logger.error(throwable);
				}
			}
		};

		ScheduledFuture<?> future = SCHEDULED_POOL.schedule(() -> EXECUTOR_POOL.execute(task::run), delay.toMilliseconds(), TimeUnit.MILLISECONDS);
		task.setFuture(future);
		return task;
	}

	public static @NotNull CancelableTimeTask runTaskTimer(@NotNull Lambda executor, Time period) {
		CancelableTimeTask task = new CancelableTimeTask() {
			@Override
			public void execute() {
				try {
					executor.run();
				} catch (Throwable throwable) {
					Logger.error(throwable);
				}
			}
		};

		ScheduledFuture<?> future = SCHEDULED_POOL.scheduleAtFixedRate(() -> EXECUTOR_POOL.execute(task::run), 0, period.toMilliseconds(), TimeUnit.MILLISECONDS);
		task.setFuture(future);
		return task;
	}

	public static @NotNull CompletableFuture<Void> runTaskAsync(@NotNull Lambda executor) {
		return CompletableFuture.runAsync(() -> {
			try {
				executor.run();
			} catch (Throwable throwable) {
				Logger.error(throwable);
			}
		}, EXECUTOR_POOL);
	}

	public static @NotNull <R> CompletableFuture<R> runTaskAsync(@NotNull ReturnLambda<R> executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return executor.run();
			} catch (Throwable throwable) {
				Logger.error(throwable);
				return null;
			}
		}, EXECUTOR_POOL);
	}
}
