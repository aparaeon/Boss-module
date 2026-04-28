package gg.mmorealms.module.chat_games.velocity.dto;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "chat_games_season_wins")
@IdClass(ChatGamesSeasonWinsId.class)
@Getter
@NoArgsConstructor
public class ChatGamesSeasonWins {

	@Id
	private UUID uuid;

	@Id
	private int seasonId;

	private long wins = 0L;
	private long lastUpdated = 0L;

	public ChatGamesSeasonWins(UUID uuid, int seasonId) {
		this.uuid = uuid;
		this.seasonId = seasonId;
	}

	public static void recordWin(UUID uuid, int seasonId) {
		try {
			DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
				ChatGamesSeasonWins entry = session.find(ChatGamesSeasonWins.class, new ChatGamesSeasonWinsId(uuid, seasonId));

				if (entry == null) {
					entry = new ChatGamesSeasonWins(uuid, seasonId);
				}

				entry.incrementWins();
				session.merge(entry);
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public void incrementWins() {
		this.wins++;
		this.lastUpdated = System.currentTimeMillis();
	}

	public void adjustWins(long delta) {
		this.wins = Math.max(0, this.wins + delta);
		this.lastUpdated = System.currentTimeMillis();
	}

	public static void adjustWins(UUID uuid, int seasonId, long delta) {
		try {
			DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
				ChatGamesSeasonWins entry = session.find(ChatGamesSeasonWins.class, new ChatGamesSeasonWinsId(uuid, seasonId));
				if (entry == null) {
					if (delta <= 0) {
						return;
					}
					entry = new ChatGamesSeasonWins(uuid, seasonId);
				}
				entry.adjustWins(delta);
				session.merge(entry);
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

}