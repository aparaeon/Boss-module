package gg.mmorealms.module.login_rewards.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import gg.mmorealms.module.core.velocity.CoreVelocityModule;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;
import gg.mmorealms.module.login_rewards.common.dto.DailyGuiDay;
import gg.mmorealms.module.login_rewards.common.dto.event.OpenDailyGUIEvent;
import gg.mmorealms.module.login_rewards.velocity.config.LoginRewardsModuleConfig;
import gg.mmorealms.module.login_rewards.velocity.dto.UserDailyLoginData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DailyManager {

	private static final long CLAIM_COOLDOWN_MS = Time.hours(24).toMilliseconds();
	private static final long CLAIM_EXPIRY_MS = Time.hours(48).toMilliseconds();

	private final Set<UUID> autoOpenedThisSession = ConcurrentHashMap.newKeySet();

	private @Inject LoginRewardsModuleConfig config;

	public boolean markAutoOpened(@NotNull UUID uuid) {
		return autoOpenedThisSession.add(uuid);
	}

	public void clearAutoOpened(@NotNull UUID uuid) {
		autoOpenedThisSession.remove(uuid);
	}

	public record ClaimResult(
		Type type,
		int finalStreak,
		boolean streakBroken,
		long remainingPlaytimeMs,
		int claimDay
	) {

		public enum Type {
			CLAIMED,
			ALREADY_CLAIMED,
			NOT_ENOUGH_PLAYTIME,
			CLAIM_FAILED
		}

		private static ClaimResult claimed(int finalStreak, boolean streakBroken) {
			return new ClaimResult(Type.CLAIMED, finalStreak, streakBroken, 0L, 0);
		}

		private static ClaimResult notEnoughPlaytime(long remainingPlaytimeMs, int claimDay) {
			return new ClaimResult(Type.NOT_ENOUGH_PLAYTIME, 0, false, remainingPlaytimeMs, claimDay);
		}

		private static ClaimResult alreadyClaimed() {
			return new ClaimResult(Type.ALREADY_CLAIMED, 0, false, 0L, 0);
		}

		private static ClaimResult claimFailed() {
			return new ClaimResult(Type.CLAIM_FAILED, 0, false, 0L, 0);
		}
	}

	public StreakAdjustment adjustStreak(@NotNull UUID uuid, int delta) {
		UserDailyLoginData daily = UserDailyLoginData.getByUUID(uuid);
		UserStats stats = UserStats.getByUUID(uuid);
		long activePlaytimeMs = getActivePlaytimeMs(stats);
		long nowMs = System.currentTimeMillis();

		int previous = daily.getStreak() + daily.getPendingClaims();
		autoOpenedThisSession.remove(uuid);

		if (delta > 0) {
			daily.setPendingClaims(daily.getPendingClaims() + delta);
			daily.setLastClaimTimestamp(nowMs);
			daily.resetClaimWindowBaseline(activePlaytimeMs, nowMs);
		} else {
			daily.setPendingClaims(0);
			daily.setStreak(Math.max(0, previous + delta));
			daily.setLastClaimTimestamp(0L);
			daily.resetClaimWindowBaseline(activePlaytimeMs, nowMs);
		}

		return new StreakAdjustment(previous, delta, daily.getStreak() + daily.getPendingClaims());
	}

	public @NotNull DailyStatus getStatus(@NotNull UUID uuid) {
		UserDailyLoginData daily = UserDailyLoginData.getByUUID(uuid);
		UserStats stats = UserStats.getByUUID(uuid);
		long activePlaytimeMs = getActivePlaytimeMs(stats);

		ClaimContext context = prepareDailyStatus(daily, activePlaytimeMs);
		int claimDay = context.claimDay();
		LoginRewardsModuleConfig.DailyDayConfig dayConfig = config.getDailyDayConfig(claimDay);
		long windowPlaytimeMs = daily.getWindowPlaytimeMs(activePlaytimeMs);

		return new DailyStatus(
			daily,
			claimDay,
			windowPlaytimeMs,
			dayConfig.getRequiredPlaytimeMs(),
			context.alreadyClaimed(),
			context.streakBroken(),
			context.previousStreak(),
			context.claimAvailableAtMs(),
			context.claimExpiresAtMs(),
			stats != null
		);
	}

	public void openDailyGUI(@NotNull Player player) {
		UserDailyLoginData daily = UserDailyLoginData.getByPlayer(player);
		UserStats stats = UserStats.getByPlayer(player);
		long activePlaytimeMs = getActivePlaytimeMs(stats);

		ClaimContext context = prepareDailyStatus(daily, activePlaytimeMs);
		int claimDay = context.claimDay();
		LoginRewardsModuleConfig.DailyDayConfig claimDayConfig = config.getDailyDayConfig(claimDay);
		long todayPlaytimeMs = daily.getWindowPlaytimeMs(activePlaytimeMs);
		long requiredPlaytimeMs = claimDayConfig.getRequiredPlaytimeMs();
		boolean canClaim = !context.alreadyClaimed()
			&& (hasPendingClaims(daily) || hasRequiredPlaytime(todayPlaytimeMs, requiredPlaytimeMs));

		new OpenDailyGUIEvent(
			player.getUniqueId(),
			daily.getStreak(),
			claimDay,
			todayPlaytimeMs,
			requiredPlaytimeMs,
			claimDayConfig.requiredInventorySpace,
			context.alreadyClaimed(),
			canClaim,
			context.streakBroken(),
			context.previousStreak(),
			context.claimAvailableAtMs(),
			context.claimExpiresAtMs(),
			buildGUIDays(daily, context, claimDay, canClaim)
		).send();
	}

	public ClaimResult markClaimedFromBackend(@NotNull UUID uuid, int claimDay) {
		UserDailyLoginData daily = UserDailyLoginData.getByUUID(uuid);
		UserStats stats = UserStats.getByUUID(uuid);
		long activePlaytimeMs = getActivePlaytimeMs(stats);
		ClaimContext context = prepareDailyStatus(daily, activePlaytimeMs);
		if (context.alreadyClaimed()) {
			return ClaimResult.alreadyClaimed();
		}

		if (context.claimDay() != claimDay) {
			return ClaimResult.claimFailed();
		}

		boolean hasPendingClaim = hasPendingClaims(daily);
		if (!hasPendingClaim) {
			LoginRewardsModuleConfig.DailyDayConfig dayConfig = config.getDailyDayConfig(claimDay);
			long todayPlaytimeMs = daily.getWindowPlaytimeMs(activePlaytimeMs);
			long requiredPlaytimeMs = dayConfig.getRequiredPlaytimeMs();
			if (!hasRequiredPlaytime(todayPlaytimeMs, requiredPlaytimeMs)) {
				return ClaimResult.notEnoughPlaytime(requiredPlaytimeMs - todayPlaytimeMs, claimDay);
			}
		}

		boolean streakBroken = context.streakBroken();
		daily.setStreak(claimDay);
		long nowMs = System.currentTimeMillis();
		if (hasPendingClaim) {
			daily.setPendingClaims(daily.getPendingClaims() - 1);
			if (daily.getPendingClaims() == 0) {
				daily.setLastClaimTimestamp(nowMs - CLAIM_COOLDOWN_MS);
			}
		} else {
			daily.setLastClaimTimestamp(nowMs);
			daily.resetClaimWindowBaseline(activePlaytimeMs, nowMs);
		}
		autoOpenedThisSession.remove(uuid);

		return ClaimResult.claimed(daily.getStreak(), streakBroken);
	}

	public void dispatchRewardCommands(@NotNull Player player, int claimDay, @NotNull EngineServer server) {
		LoginRewardsModuleConfig.DailyDayConfig dayConfig = config.getDailyDayConfig(claimDay);
		List<String> commands = dayConfig.commands.parse("player", player.getUsername()).parse();

		Logger.debug("Dispatching " + commands.size() + " daily reward command(s) for "
			+ player.getUsername() + " day " + claimDay + " to backend " + server.getRedisID() + ".");

		for (String command : commands) {
			CommandExecuteEvent.onBackend(server.getRedisID(), command).send();
		}
	}

	public @Nullable EngineServer getRewardDispatchServer(@NotNull Player player) {
		ServerConnection connection = player.getCurrentServer().orElse(null);
		if (connection == null) {
			Logger.warn("Could not dispatch daily reward commands for " + player.getUniqueId() + ": no current server.");
			return null;
		}

		EngineServer server = CoreVelocityModule.instance().getServerManager()
			.getServer(connection.getServerInfo().getName());
		if (server == null) {
			Logger.warn("Could not dispatch daily reward commands for " + player.getUniqueId() + ": no EngineServer for " + connection.getServerInfo().getName() + ".");
			return null;
		}

		return server;
	}

	private List<DailyGuiDay> buildGUIDays(UserDailyLoginData daily, ClaimContext context, int claimDay, boolean canClaim) {
		int windowSize = Math.max(1, config.dailyGuiDaysToShow);
		int windowStart = 1;
		int windowEnd = windowStart + windowSize - 1;
		List<DailyGuiDay> days = new ArrayList<>();
		int streak = daily.getStreak();
		int pendingClaims = daily.getPendingClaims();
		int pendingEndDay = streak + pendingClaims;
		int currentDay = pendingClaims > 0 ? pendingEndDay + 1 : claimDay;
		boolean streakBroken = context.streakBroken();

		for (int day = windowStart; day <= windowEnd; day++) {
			LoginRewardsModuleConfig.DailyDayConfig dayConfig = config.getDailyDayConfig(day);
			int rewardDay = config.resolveDailyRewardDay(day);
			boolean current = day == currentDay;
			boolean claimed = !streakBroken && day <= streak;
			boolean claimable = !streakBroken
				&& !claimed
				&& (day <= pendingEndDay || (pendingClaims == 0 && day == claimDay && canClaim));

			days.add(new DailyGuiDay(
				day,
				rewardDay,
				dayConfig.requiredPlaytime == null ? "" : formatDuration(dayConfig.getRequiredPlaytimeMs()),
				config.getDisplayJson(dayConfig),
				dayConfig.rewardLore,
				current,
				claimed,
				claimable
			));
		}

		return days;
	}

	private static ClaimContext getClaimContext(UserDailyLoginData daily, long nowMs) {
		if (daily.getLastClaimTimestamp() == 0L) {
			return new ClaimContext(Math.max(1, daily.getStreak() + 1), false, false, 0, 0L, 0L);
		}

		long elapsedMs = Math.max(0L, nowMs - daily.getLastClaimTimestamp());
		long claimAvailableAtMs = daily.getLastClaimTimestamp() + CLAIM_COOLDOWN_MS;
		long claimExpiresAtMs = daily.getLastClaimTimestamp() + CLAIM_EXPIRY_MS;

		if (elapsedMs > CLAIM_EXPIRY_MS) {
			return new ClaimContext(1, true, false, daily.getStreak(), claimAvailableAtMs, claimExpiresAtMs);
		}

		if (hasPendingClaims(daily)) {
			return new ClaimContext(Math.max(1, daily.getStreak() + 1), false, false, 0, claimAvailableAtMs, claimExpiresAtMs);
		}

		if (elapsedMs < CLAIM_COOLDOWN_MS) {
			return new ClaimContext(Math.max(1, daily.getStreak()), false, true, 0, claimAvailableAtMs, claimExpiresAtMs);
		}

		return new ClaimContext(daily.getStreak() + 1, false, false, 0, claimAvailableAtMs, claimExpiresAtMs);
	}

	private ClaimContext prepareDailyStatus(UserDailyLoginData daily, long activePlaytimeMs) {
		long nowMs = System.currentTimeMillis();
		ClaimContext context = applyMissedStreakResetIfNeeded(daily, activePlaytimeMs, nowMs);

		if (!context.alreadyClaimed() && !context.streakBroken() && !hasPendingClaims(daily) && daily.getLastClaimTimestamp() > 0L) {
			if (daily.getClaimWindowStartTimestamp() < daily.getLastClaimTimestamp()) {
				// Snapshot baseline at observation moment — UserStats only exposes cumulative
				// afkTime, so reconstructing as-of-T causes AFK between T and now to be counted
				// as active. Maybe revisit when the new AFK module potentially exposes per-timestamp AFK.
				daily.resetClaimWindowBaseline(activePlaytimeMs, nowMs);
			}
		}

		if (daily.getLastClaimTimestamp() == 0L && daily.getClaimWindowStartTimestamp() == 0L) {
			daily.resetClaimWindowBaseline(activePlaytimeMs, nowMs);
		}

		return context;
	}

	private static long getActivePlaytimeMs(@Nullable UserStats stats) {
		if (stats == null) {
			return 0L;
		}

		return Math.max(0L, stats.getOnlineTime() + stats.getOnlineTimeThisSession() - stats.getAfkTime());
	}

	private static boolean hasRequiredPlaytime(long playtimeMs, long requiredMs) {
		return playtimeMs + 999L >= requiredMs;
	}

	private static boolean hasPendingClaims(UserDailyLoginData daily) {
		return daily.getPendingClaims() > 0;
	}

	private ClaimContext applyMissedStreakResetIfNeeded(UserDailyLoginData daily, long activePlaytimeMs, long nowMs) {
		ClaimContext context = getClaimContext(daily, nowMs);

		if (!context.streakBroken()) {
			return context;
		}

		daily.setStreak(0);
		daily.setPendingClaims(0);
		daily.setLastClaimTimestamp(0L);
		daily.resetClaimWindowBaseline(activePlaytimeMs, nowMs);
		autoOpenedThisSession.remove(daily.getIdentifier());

		return new ClaimContext(1, true, false, context.previousStreak(), context.claimAvailableAtMs(), context.claimExpiresAtMs());
	}

	public record ClaimContext(
		int claimDay,
		boolean streakBroken,
		boolean alreadyClaimed,
		int previousStreak,
		long claimAvailableAtMs,
		long claimExpiresAtMs
	) {
	}

	public record DailyStatus(
		UserDailyLoginData daily,
		int claimDay,
		long todayPlaytimeMs,
		long requiredPlaytimeMs,
		boolean alreadyClaimed,
		boolean streakBroken,
		int previousStreak,
		long claimAvailableAtMs,
		long claimExpiresAtMs,
		boolean playtimeKnown
	) {
		public boolean shouldAutoOpen() {
			return playtimeKnown && !alreadyClaimed && (hasPendingClaims(daily) || todayPlaytimeMs >= requiredPlaytimeMs);
		}
	}

	public record StreakAdjustment(int previousStreak, int delta, int currentStreak) {
	}

	public static String formatAvailableIn(DailyStatus status, long nowMs) {
		long cooldownRemainingMs = status.claimAvailableAtMs() - nowMs;
		long playtimeRemainingMs = status.requiredPlaytimeMs() - status.todayPlaytimeMs();
		return formatCountdown(Math.max(cooldownRemainingMs, playtimeRemainingMs));
	}

	public static String formatCountdown(long ms) {
		if (ms <= 0L) {
			return "now";
		}
		return formatDuration(ms);
	}

	public static String formatDuration(long ms) {
		// Round down to whole seconds so sub-second residues don't surface as "Xms" in player-facing text.
		return Time.seconds(Math.max(0L, ms) / 1000L).toString();
	}
}
