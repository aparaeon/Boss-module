package gg.mmorealms.loader.backend.common.mixin;

import com.google.common.collect.Streams;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.backend.common.dto.ShutdownEvent;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.error.WatchdogError;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.sampler.java.MergeStrategy;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.proto.SparkSamplerProtos;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerWatchdog;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Mixin(ServerWatchdog.class)
public abstract class ServerWatchdogMixin {
	@Shadow
	@Final
	private DedicatedServer server;
	@Shadow
	@Final
	private long maxTickTimeNanos;

	@Shadow
	protected abstract void exit();

	@Unique
	private static final Time SAFELY_KILL_TIMER = Time.minutes(10);

	@Unique
	private static final List<String> FILTERED_OUT_THREADS = List.of(
			"luckperms",
			"spark",
			"ConfigSaver",
			"Write-Updater",
			"Read-Updater",
			"VoiceChatServerThread",
			"Common-Cleaner",
			"Read-Poller",
			"Server console handler"
	);

	@Unique
	private static final List<String> ALLOWED_PACKAGES = List.of(
			"gg.mmorealms",
			"net.minecraft"
	);

	@Unique
	private static final String CRASH_REPORTS_RELATIVE_PATH = "../../crash-reports";

	@Unique
	@SuppressWarnings("SameParameterValue")
	private static Path loader$getCrashReportsFile(String fileName) {
		return Path.of(BackendLoader.instance().getFileManager().getDataFolder().toPath().toString(), CRASH_REPORTS_RELATIVE_PATH, fileName);
	}

	@Unique
	private static void loader$writeAdditionalCrashReport(String fileName, String content) {
		BackendLoader.instance().getFileManager().writeFile(CRASH_REPORTS_RELATIVE_PATH, fileName, content);
	}

	/**
	 * @author Madalin-Andrei Coman (Alkatraz)
	 * @reason Modified the watchdog to send ServerForceCacheCommitEvent when it would crash the server and then
	 * wait a certain time before continuing the default logic
	 */
	@Overwrite
	public void run() {
		while (this.server.isRunning()) {
			long nextTickTime = this.server.getNextTickTime();
			long currentTimeNanos = Util.getNanos();
			long tickTime = currentTimeNanos - nextTickTime;
			if (tickTime < 0) {
				tickTime = 0;
			}

			Logger.info(new MessageBuilder("Tick time: {milliseconds}ms")
					.parse("milliseconds", (tickTime / TimeUtil.NANOSECONDS_PER_MILLISECOND))
					.parse("nanoseconds", tickTime));

			if (tickTime < this.maxTickTimeNanos) {
				try {
					//noinspection BusyWait
					Thread.sleep((nextTickTime + this.maxTickTimeNanos - currentTimeNanos) / TimeUtil.NANOSECONDS_PER_MILLISECOND);
				} catch (InterruptedException ignored) {
				}
				continue;
			}

			Logger.error("Watchdog crash imminent. Begin cache commiting");

			//noinspection unused
			CancelableTimeTask cancelableTimeTask = ScheduleUtils.runTaskLater(() -> {
				Logger.error("Watchdog crash safe timer elapsed, continuing with crash process.");
				this.exit();
			}, SAFELY_KILL_TIMER);

			int playerCount = BackendLoader.instance().getServer().getPlayerList().getPlayers().size();
			List<String> playerNames = BackendLoader.instance().getServer().getPlayerList().getPlayers().stream()
					.map(ServerPlayer::getName)
					.map(Component::getString)
					.toList();

			loader$writeAdditionalCrashReport(
					"players.txt",
					new MessageBuilder(
							"""
									Players Count: {count}
									Players: {names}
									"""
					)
							.parse("count", playerCount)
							.parse("names", String.join(", ", playerNames))
							.parse()

			);


			Logger.debug("Sending Shutdown Event...");
			new ShutdownEvent().fireSync();
			Logger.debug("All shutdown listeners have been executed, proceeding with cache commit.");

			CountDownLatch chunkSaveLatch = new CountDownLatch(1);

			if (BackendLoader.instance().getServerType() == ServerType.REALMS) {
				Logger.debug("Saving all chunks...");
				this.server.execute(() -> {
					BackendLoader.instance().getServer().saveAllChunks(true, true, true);
					chunkSaveLatch.countDown();
				});
				Logger.debug("All chunks saved");
			} else {
				Logger.debug("Skipping chunk saving...");
				chunkSaveLatch.countDown();

			}

			try {
				chunkSaveLatch.await();
			} catch (InterruptedException exception) {
				Logger.error(exception);
				this.exit();
			}

			HashMap<String, StackTraceElement[]> before = loader$handleStackTraces("before");

			for (DatabaseLoader<?, ?, ?> databaseLoader : DatabaseLoader.getALL()) {
				if (databaseLoader.getTableName().equalsIgnoreCase("realms")) {
					continue; // We skip realms saving for optimizations reason, as the worlds can finish saving after we disconnect the players
				}
				databaseLoader.clearAllCache(true);
			}

			for (ServerPlayer player : BackendLoader.instance().getServer().getPlayerList().getPlayers()) {
				player.connection.disconnect(BackendLoader.instance().getMiniMessageManager().parse("Server Crashed"));
			}

			for (DatabaseLoader<?, ?, ?> databaseLoader : DatabaseLoader.getALL()) {
				if (!databaseLoader.getTableName().equalsIgnoreCase("realms")) {
					continue; // We skip any other table other than realms as they were already saved above
				}
				databaseLoader.clearAllCache(true);
			}


			HashMap<String, StackTraceElement[]> after = loader$handleStackTraces("after");
			HashMap<String, StackTraceElement[]> delta = loader$findOverlapping(before, after);
			loader$handleStackTraces("delta", delta);

			loader$createSparkReport();

			loader$defaultLogic(tickTime);
		}
	}

	@Unique
	private void loader$createSparkReport() {
		SparkPlatform platform = BackendLoader.instance().getSparkPlatform();
		Sampler sampler = platform.getSamplerContainer().getActiveSampler();

		if (sampler == null) {
			Logger.error("There was no active spark profiler at this time.");
			return;
		}

		platform.getSamplerContainer().unsetActiveSampler(sampler);
		sampler.stop(false);

		this.loader$uploadProfiler(platform, sampler);
	}


	@Unique
	private HashMap<String, StackTraceElement[]> loader$handleStackTraces(String header) {
		HashMap<String, StackTraceElement[]> unfilteredThreads = loader$getActiveThreads();
		return loader$handleStackTraces(header, unfilteredThreads);
	}

	@Unique
	private HashMap<String, StackTraceElement[]> loader$handleStackTraces(String header, HashMap<String, StackTraceElement[]> unfilteredThreads) {
		HashMap<String, StackTraceElement[]> filteredThreads = loader$filterThreads(unfilteredThreads);
		String unfilteredLog = loader$generateThreadDumps(header.toUpperCase(), unfilteredThreads);
		String filteredLog = loader$generateThreadDumps(header.toUpperCase(), filteredThreads);

		Logger.error("\n\n\n\n\n\n\n\n\n\n" + unfilteredLog + "\n\n\n\n\n\n\n\n\n\n");

		loader$writeAdditionalCrashReport(header.toLowerCase() + "_unfiltered.txt", unfilteredLog);
		loader$writeAdditionalCrashReport(header.toLowerCase() + "_filtered.txt", filteredLog);

		return unfilteredThreads;
	}

	@Unique
	private HashMap<String, StackTraceElement[]> loader$getActiveThreads() {
		HashMap<String, StackTraceElement[]> result = new HashMap<>();

		Thread.getAllStackTraces().forEach((thread, stackTrace) -> {
			result.put(thread.getName(), stackTrace);
		});

		return result;
	}

	@Unique
	private HashMap<String, StackTraceElement[]> loader$filterThreads(HashMap<String, StackTraceElement[]> stackTraces) {
		HashMap<String, StackTraceElement[]> filteredStackTraces = new HashMap<>();

		stackTraces.forEach((thread, stackTrace) -> {
			boolean filterOut = false;

			for (String filteredOutThread : FILTERED_OUT_THREADS) {
				if (thread.toLowerCase().contains(filteredOutThread.toLowerCase())) {
					filterOut = true;
					break;
				}
			}

			if (!filterOut) {
				boolean foundPackage = false;
				for (String allowedPackage : ALLOWED_PACKAGES) {
					for (StackTraceElement element : stackTrace) {
						if (element.getClassName().toLowerCase().contains(allowedPackage.toLowerCase())) {
							foundPackage = true;
							break;
						}
					}
				}

				if (!foundPackage) {
					filterOut = true;
				}
			}

			if (!filterOut) {
				filteredStackTraces.put(thread, stackTrace);
			}
		});

		return filteredStackTraces;
	}

	@Unique
	private HashMap<String, StackTraceElement[]> loader$findOverlapping(HashMap<String, StackTraceElement[]> stackTraces1, HashMap<String, StackTraceElement[]> stackTraces2) {
		HashMap<String, StackTraceElement[]> result = new HashMap<>();

		for (String threadName : stackTraces1.keySet()) {
			if (stackTraces2.containsKey(threadName)) {
				StackTraceElement[] stackTrace1 = stackTraces1.get(threadName);
				StackTraceElement[] stackTrace2 = stackTraces2.get(threadName);

				boolean overlap = false;
				for (StackTraceElement stackElement1 : stackTrace1) {
					for (StackTraceElement stackElement2 : stackTrace2) {
						if (stackElement1.equals(stackElement2)) {
							overlap = true;
							break;
						}
					}
					if (overlap) {
						break;
					}
				}

				if (overlap) {
					result.put(threadName, stackTrace1);
				}
			}
		}

		return result;
	}


	@Unique
	private String loader$generateThreadDumps(String header, HashMap<String, StackTraceElement[]> threads) {
		StringBuilder output = new StringBuilder();
		output.append("=============== START THREAD DUMP ").append(header).append(" ===============").append("\n");

		threads.forEach((threadName, stackTrace) -> {
			output.append(threadName).append(":").append("\n");

			for (StackTraceElement element : stackTrace) {
				output.append("\t").append(element).append("\n");
			}
		});

		output.append("=============== END THREAD DUMP ").append(header).append(" ===============").append("\n");
		return output.toString();
	}

	@Unique
	private Sampler.ExportProps loader$getExportProps(SparkPlatform platform) {
		return (new Sampler.ExportProps())
				.creator(new CommandSender.Data("CONSOLE", null))
				.comment("watchdog trigger crash")
				.mergeStrategy(
						MergeStrategy.SEPARATE_PARENT_CALLS)
				.classSourceLookup(() -> ClassSourceLookup.create(platform));
	}

	@Unique
	public void loader$uploadProfiler(SparkPlatform platform, Sampler sampler) {
		Sampler.ExportProps exportProps = this.loader$getExportProps(platform);
		SparkSamplerProtos.SamplerData output = sampler.toProto(platform, exportProps);

		Path file = loader$getCrashReportsFile("report.sparkprofile");

		try {
			Files.write(file, output.toByteArray());
		} catch (IOException exception) {
			Logger.error("There was an error while writing spark report file");
			Logger.error(exception);
		}
	}

	@Unique
	private void loader$defaultLogic(long tickTime) {
		Logger.error(new MessageBuilder("A single server tick took {single} seconds (should be max {max})")
				.parse("single", String.format("%.2f", (float) tickTime / (float) TimeUtil.NANOSECONDS_PER_SECOND))
				.parse("max", String.format("%.2f", this.server.tickRateManager().millisecondsPerTick() / (float) TimeUtil.MILLISECONDS_PER_SECOND)));
		Logger.error("Considering it to be crashed, server will forcibly shutdown.");
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
		StringBuilder stringBuilder = new StringBuilder();
		Error error = new WatchdogError();

		for (ThreadInfo threadInfo : threadInfos) {
			if (threadInfo.getThreadId() == this.server.getRunningThread().threadId()) {
				error.setStackTrace(threadInfo.getStackTrace());
			}

			stringBuilder.append(threadInfo);
			stringBuilder.append("\n");
		}

		CrashReport crashReport = new CrashReport("Watching Server", error);
		this.server.fillSystemReport(crashReport.getSystemReport());
		CrashReportCategory threadDumpCrashCategory = crashReport.addCategory("Thread Dump");
		threadDumpCrashCategory.setDetail("Threads", stringBuilder);
		CrashReportCategory performanceStatsCrashCategory = crashReport.addCategory("Performance stats");
		performanceStatsCrashCategory.setDetail("Random tick rate", () -> this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).toString());
		performanceStatsCrashCategory.setDetail("Level stats", () -> Streams.stream(this.server.getAllLevels())
				.map((serverLevel) -> serverLevel.dimension() + ": " + serverLevel.getWatchdogStats())
				.collect(Collectors.joining(",\n")));
		Bootstrap.realStdoutPrintln("Crash report:\n" + crashReport.getFriendlyReport(ReportType.CRASH));
		Path path = this.server.getServerDirectory().resolve("crash-reports").resolve("crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
		if (crashReport.saveToFile(path, ReportType.CRASH)) {
			Logger.error(new MessageBuilder("This crash report has been saved to: {crash_file_path}")
					.parse("crash_file_path", path.toAbsolutePath()));
		} else {
			Logger.error("We were unable to save this crash report to disk.");
		}

		this.exit();
	}
}
