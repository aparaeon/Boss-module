package gg.mmorealms.module.chat_games.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.chat_games.common.dto.LeaderboardEntry;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;
import gg.mmorealms.module.chat_games.velocity.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.velocity.dto.ChatGamesSeasonWins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LeaderboardManager {

	private @Inject ChatGamesConfig config;

	private CancelableTimeTask task;

	private volatile List<LeaderboardEntry> overallLeaderboard = Collections.emptyList();
	private volatile List<LeaderboardEntry> seasonLeaderboard = Collections.emptyList();

	private static List<LeaderboardEntry> buildEntriesFromAggregation(List<Object[]> results) {
		List<LeaderboardEntry> entries = new ArrayList<>();
		int placement = 0;

		for (Object[] row : results) {
			UUID uuid = (UUID) row[0];
			long wins = (Long) row[1];

			if (uuid == null) {
				continue;
			}

			placement++;
			entries.add(new LeaderboardEntry(uuid, MojangUtils.getUsernameOrUUID(uuid), wins, placement));
		}

		return List.copyOf(entries);
	}

	static List<LeaderboardEntry> buildEntriesFromSeasonWins(List<ChatGamesSeasonWins> results) {
		List<LeaderboardEntry> entries = new ArrayList<>();
		int placement = 0;

		for (ChatGamesSeasonWins data : results) {
			UUID uuid = data.getUuid();
			long wins = data.getWins();

			if (uuid == null) {
				continue;
			}

			placement++;
			entries.add(new LeaderboardEntry(uuid, MojangUtils.getUsernameOrUUID(uuid), wins, placement));
		}

		return List.copyOf(entries);
	}

	public List<LeaderboardEntry> getOverallLeaderboard() {
		return overallLeaderboard;
	}

	public List<LeaderboardEntry> getSeasonLeaderboard() {
		return seasonLeaderboard;
	}

	public int getOverallPlacement(UUID playerUuid) {
		return placementIn(overallLeaderboard, playerUuid);
	}

	public int getSeasonPlacement(UUID playerUuid) {
		return placementIn(seasonLeaderboard, playerUuid);
	}

	public void clearSeasonCache() {
		seasonLeaderboard = Collections.emptyList();
	}

	public void refresh() {
		int size = config.season.leaderboardSize;
		int snapshotSeasonId = ChatGamesVelocityModule.instance().getSeasonManager().getCurrentSeasonId();

		// Run both queries and close their sessions before resolving usernames,
		List<Object[]> overallResults = DatabaseManager.instance().getSessionFactory().fromTransaction(session ->
			session.createQuery(
					"SELECT uuid, SUM(wins) FROM chat_games_season_wins GROUP BY uuid HAVING SUM(wins) > 0 ORDER BY SUM(wins) DESC, MIN(lastUpdated) ASC, uuid ASC",
					Object[].class)
				.setMaxResults(size)
				.getResultList()
		);

		List<ChatGamesSeasonWins> seasonResults = DatabaseManager.instance().getSessionFactory().fromTransaction(session ->
			session.createQuery(
					"FROM chat_games_season_wins WHERE seasonId = :sid AND wins > 0 ORDER BY wins DESC, lastUpdated ASC, uuid ASC",
					ChatGamesSeasonWins.class)
				.setParameter("sid", snapshotSeasonId)
				.setMaxResults(size)
				.getResultList()
		);

		overallLeaderboard = buildEntriesFromAggregation(overallResults);

		if (ChatGamesVelocityModule.instance().getSeasonManager().getCurrentSeasonId() == snapshotSeasonId) {
			seasonLeaderboard = buildEntriesFromSeasonWins(seasonResults);
		}
	}

	public void schedule() {
		task = ScheduleUtils.runTaskTimer(this::refresh, Time.minutes(10));
	}

	private int placementIn(List<LeaderboardEntry> leaderboard, UUID playerUuid) {
		return leaderboard.stream()
			.filter(e -> e.getUuid().equals(playerUuid))
			.findFirst()
			.map(LeaderboardEntry::getPlacement)
			.orElse(-1);
	}

}
