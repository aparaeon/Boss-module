package gg.mmorealms.module.chat_games.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.chat.common.dto.GlobalMessageEvent;
import gg.mmorealms.module.chat_games.common.dto.ChatGamesPendingReward;
import gg.mmorealms.module.chat_games.common.dto.LeaderboardEntry;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;
import gg.mmorealms.module.chat_games.velocity.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.velocity.config.SeasonReward;
import gg.mmorealms.module.chat_games.velocity.dto.ChatGamesSeasonWins;
import gg.mmorealms.module.chat_games.velocity.dto.SeasonState;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SeasonManager {

	private @Inject ChatGamesConfig config;
	private @Inject FileManager fileManager;
	private @Inject LeaderboardManager leaderboardManager;

	private CancelableTimeTask task;
	private final AtomicBoolean resetInProgress = new AtomicBoolean(false);
	@Getter
	private volatile int currentSeasonId = 1;

	public void schedule() {
		bootstrapState();
		reconcileSeasonId();
		task = ScheduleUtils.runTaskTimer(this::checkReset, Time.hours(1));
	}

	private void reconcileSeasonId() {
		SeasonState state = fileManager.load(SeasonState.class);
		int fileSeasonId = state.getCurrentSeasonId();

		Integer dbMax = DatabaseManager.instance().getSessionFactory().fromTransaction(session ->
			session.createQuery("SELECT MAX(seasonId) FROM chat_games_season_wins", Integer.class)
				.uniqueResult()
		);
		int dbMaxInt = dbMax == null ? 0 : dbMax;

		Logger.info("[ChatGames] Season state on startup: file.currentSeasonId=" + fileSeasonId
			+ ", db.maxSeasonId=" + (dbMax == null ? "none" : dbMax)
			+ ", lastResetDate=" + state.getLastResetDate());

		if (fileSeasonId <= 0) {
			int recoveredId = Math.max(1, dbMaxInt);
			Logger.warn("[ChatGames] season_state.json is fresh. Recovering season from DB: " + recoveredId);
			state.setCurrentSeasonId(recoveredId);
			fileManager.save(state);
		} else if (dbMaxInt > 0 && fileSeasonId < dbMaxInt) {
			Logger.warn("[ChatGames] season_state.json currentSeasonId=" + fileSeasonId
				+ " is behind DB max=" + dbMaxInt + ". Recovering to " + dbMaxInt);
			state.setCurrentSeasonId(dbMaxInt);
			fileManager.save(state);
		}

		loadCurrentSeasonId();
	}

	public void loadCurrentSeasonId() {
		currentSeasonId = fileManager.load(SeasonState.class).getCurrentSeasonId();
	}

	private void bootstrapState() {
		SeasonState state = fileManager.load(SeasonState.class);
		if (!state.getLastResetDate().isBlank()) {
			return;
		}

		LocalDate today = LocalDate.now(ZoneId.of(config.season.timezone));
		state.setCurrentSeasonName(today.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
		state.setLastResetDate(getLastExpectedReset(today).toString());
		fileManager.save(state);
	}

	public boolean forceReset() {
		return scheduleReset(true);
	}

	private void checkReset() {
		if (!config.season.enabled) {
			return;
		}
		scheduleReset(false);
	}

	private boolean scheduleReset(boolean force) {
		if (!resetInProgress.compareAndSet(false, true)) {
			return false;
		}

		ScheduleUtils.runTaskAsync(() -> {
			try {
				executeReset(force);
			} finally {
				resetInProgress.set(false);
			}
		});
		return true;
	}

	private void executeReset(boolean force) {
		ChatGamesManager chatGamesManager = ChatGamesVelocityModule.instance().getChatGamesManager();
		if (chatGamesManager != null) {
			chatGamesManager.closeActiveGameForSeasonReset();
		}

		SeasonState state = fileManager.load(SeasonState.class);

		LocalDate now = LocalDate.now(ZoneId.of(config.season.timezone));
		LocalDate expectedReset = getLastExpectedReset(now);
		if (state.getLastResetDate().isBlank()) {
			state.setLastResetDate(expectedReset.toString());
			fileManager.save(state);
			loadCurrentSeasonId();
			return;
		}
		LocalDate lastReset = LocalDate.parse(state.getLastResetDate());

		if (!force && !expectedReset.isAfter(lastReset)) {
			return;
		}

		int endingSeasonId = state.getCurrentSeasonId();

		int[] newSeasonIdHolder = new int[1];
		List<ChatGamesSeasonWins> results = DatabaseManager.instance().getSessionFactory().fromTransaction(session -> {
			List<ChatGamesSeasonWins> wins = session.createQuery(
					"FROM chat_games_season_wins WHERE seasonId = :sid AND wins > 0 ORDER BY wins DESC, lastUpdated ASC, uuid ASC",
					ChatGamesSeasonWins.class)
				.setParameter("sid", endingSeasonId)
				.setMaxResults(config.season.leaderboardSize)
				.getResultList();

			Integer dbMax = session.createQuery("SELECT MAX(seasonId) FROM chat_games_season_wins", Integer.class)
				.uniqueResult();
			newSeasonIdHolder[0] = Math.max(endingSeasonId, dbMax == null ? 0 : dbMax) + 1;

			return wins;
		});

		List<LeaderboardEntry> winners = LeaderboardManager.buildEntriesFromSeasonWins(results);
		int newSeasonId = newSeasonIdHolder[0];

		state.setCurrentSeasonId(newSeasonId);
		state.setCurrentSeasonName(now.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
		state.setLastResetDate(now.toString());
		fileManager.save(state);

		Logger.info("[ChatGames] Season reset: endingSeasonId=" + endingSeasonId
			+ ", newSeasonId=" + newSeasonId + ", winners=" + winners.size());

		loadCurrentSeasonId();
		leaderboardManager.clearSeasonCache();
		leaderboardManager.refresh();

		if (winners.isEmpty()) {
			new GlobalMessageEvent(config.season.noWinnersMessage.parse()).send();
			return;
		}

		new GlobalMessageEvent(config.season.seasonEndHeader.parse()).send();
		storePendingRewards(winners, endingSeasonId);
		new GlobalMessageEvent(config.season.seasonEndFooter.parse()).send();
	}

	private void storePendingRewards(List<LeaderboardEntry> winners, int seasonId) {
		for (LeaderboardEntry entry : winners) {
			@Nullable SeasonReward reward = getRewardForPlacement(entry.getPlacement());
			if (reward == null) {
				break;
			}

			String announcement = reward.getAnnouncement()
				.parse("player", entry.getUsername())
				.parse("placement", String.valueOf(entry.getPlacement()))
				.parse("wins", String.valueOf(entry.getWins()))
				.parse();
			new GlobalMessageEvent(announcement).send();

			ChatGamesPendingReward.store(
				entry.getUuid(),
				seasonId,
				entry.getPlacement(),
				entry.getWins()
			);
		}
	}

	private LocalDate getLastExpectedReset(LocalDate today) {
		int resetDay = Math.max(1, Math.min(config.season.resetDayOfMonth, today.lengthOfMonth()));
		LocalDate candidate = today.withDayOfMonth(resetDay);
		return candidate.isAfter(today) ? candidate.minusMonths(1) : candidate;
	}

	@Nullable
	public SeasonReward getRewardForPlacement(int placement) {
		for (SeasonReward reward : config.season.monthlyRewards) {
			if (placement >= reward.getFromPlacement() && placement <= reward.getToPlacement()) {
				return reward;
			}
		}
		return null;
	}

}
