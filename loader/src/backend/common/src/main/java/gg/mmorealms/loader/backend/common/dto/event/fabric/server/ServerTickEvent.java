package gg.mmorealms.loader.backend.common.dto.event.fabric.server;

import com.google.common.primitives.Ints;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnLambda;
import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerTickEvent extends LocalEvent {

	private static final int MAX_PRIORITY = 10;
	private static final List<List<TickTask<?>>> EXECUTORS_LISTS = new ArrayList<>(MAX_PRIORITY);
	/**
	 * LambdaExecutor, interval, ticks until next run
	 */
	@Getter
	private static final LinkedList<TickScheduledTask> SCHEDULE_TICK_EXECUTORS = new LinkedList<>();

	static {
		for (int i = 0; i < MAX_PRIORITY; i++) {
			EXECUTORS_LISTS.add(new ArrayList<>());
		}
	}

	private MinecraftServer server;

	public static <T> CompletableFuture<T> runOnTick(Lambda executor) {
		return runOnTick(executor, MAX_PRIORITY / 2);
	}

	public static <T> CompletableFuture<T> runOnTick(ReturnLambda<T> executor) {
		return runOnTick(executor, MAX_PRIORITY / 2);
	}

	public static <T> CompletableFuture<T> runOnTick(Lambda executor, int priority) {
		TickTask<T> task = new TickTask<>(() -> {
			executor.run();
			return null;
		});
		EXECUTORS_LISTS
			.get(Math.clamp(priority, 0, MAX_PRIORITY - 1))
			.add(task);
		return runOnTick(task, priority);
	}

	public static <T> CompletableFuture<T> runOnTick(ReturnLambda<T> executor, int priority) {
		TickTask<T> task = new TickTask<>(executor);
		EXECUTORS_LISTS
			.get(Math.clamp(priority, 0, MAX_PRIORITY - 1))
			.add(task);
		return runOnTick(task, priority);
	}

	public static <T> CompletableFuture<T> runOnTick(TickTask<T> task, int priority) {
		EXECUTORS_LISTS
			.get(Math.clamp(priority, 0, MAX_PRIORITY - 1))
			.add(task);
		return task.getFuture();
	}

	public static void runOnMultipleTicks(int from, int to, ArgLambda<Integer> executor) {
		runOnMultipleTicks(from, to, executor, MAX_PRIORITY / 2);
	}

	public static void runOnMultipleTicks(int from, int to, ArgLambda<Integer> executor, int order) {
		for (int i = from; i <= to; i++) {
			int finalI = i;
			runOnTick(new TickTask<>(() -> {
				executor.run(finalI);
				return null;
			}, executor.toString()), order);
		}
	}

	public static void runOnTimer(Lambda executor, Time time) {
		int interval = Ints.checkedCast(time.toMilliseconds()) / 50;
		SCHEDULE_TICK_EXECUTORS.add(new TickScheduledTask(executor, interval, interval));
	}

	public static void runOnTimer(Lambda executor, int interval) {
		SCHEDULE_TICK_EXECUTORS.add(new TickScheduledTask(executor, interval, interval));
	}

	public static List<TickTask<?>> popExecutors(int count) {
		List<TickTask<?>> output = new ArrayList<>();

		for (List<TickTask<?>> executors : EXECUTORS_LISTS) {
			while (!executors.isEmpty()) {
				output.add(executors.removeFirst());

				if (output.size() >= count) {
					return output;
				}
			}
		}

		return output;
	}

	public static int getExecutorCount() {
		int total = 0;

		for (List<TickTask<?>> executorsList : EXECUTORS_LISTS) {
			total += executorsList.size();
		}

		return total;
	}

	public static class TickTask<T> {
		private final ReturnLambda<T> executor;
		private final @Getter CompletableFuture<T> future;
		private final @Getter String name;

		public TickTask(ReturnLambda<T> executor) {
			this(executor, executor.toString());
		}

		public TickTask(ReturnLambda<T> executor, String name) {
			this.executor = executor;
			this.name = name;
			this.future = new CompletableFuture<>();
		}

		@Override
		public String toString() {
			return name;
		}

		public void complete() {
			T result = executor.run();
			this.future.complete(result);
		}
	}

	@AllArgsConstructor
	@Getter
	public static class TickScheduledTask {
		private final Lambda executor;
		private final Integer interval;
		private Integer timeUntilNextRun;

		/**
		 * @return Whether the task was executed this tick
		 */
		public boolean process() {
			this.timeUntilNextRun--;

			if (this.timeUntilNextRun <= 0) {
				//				Logger.debug("Executing scheduled tick task " + this.executor.toString());
				this.executor.run();
				this.timeUntilNextRun = this.interval;
				return true;
			}

			return false;
		}

		@Override
		public String toString() {
			return executor.toString();
		}
	}

}
