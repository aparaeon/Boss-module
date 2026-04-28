package gg.mmorealms.module.chat_games.common.dto;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Entity(name = "chat_games_pending_rewards")
@IdClass(ChatGamesPendingRewardId.class)
@Getter
@NoArgsConstructor
public class ChatGamesPendingReward {

	@Id
	private UUID uuid;

	@Id
	private int seasonId;

	private int placement;
	private long wins;
	private boolean claimed = false;

	public ChatGamesPendingReward(UUID uuid, int seasonId, int placement, long wins) {
		this.uuid = uuid;
		this.seasonId = seasonId;
		this.placement = placement;
		this.wins = wins;
	}

	public static void store(UUID uuid, int seasonId, int placement, long wins) {
		try {
			DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
				ChatGamesPendingReward existing = session.find(
					ChatGamesPendingReward.class, new ChatGamesPendingRewardId(uuid, seasonId));
				if (existing != null) {
					return;
				}
				session.persist(new ChatGamesPendingReward(uuid, seasonId, placement, wins));
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	@Nullable
	public static ChatGamesPendingReward getUnclaimed(UUID uuid) {
		return DatabaseManager.instance().getSessionFactory().fromTransaction(session ->
			session.createQuery(
					"FROM chat_games_pending_rewards WHERE uuid = :uuid AND claimed = false ORDER BY seasonId ASC",
					ChatGamesPendingReward.class)
				.setParameter("uuid", uuid)
				.setMaxResults(1)
				.uniqueResult()
		);
	}

	public static long countUnclaimed(UUID uuid) {
		return DatabaseManager.instance().getSessionFactory().fromTransaction(session ->
			session.createQuery(
					"SELECT COUNT(*) FROM chat_games_pending_rewards WHERE uuid = :uuid AND claimed = false",
					Long.class)
				.setParameter("uuid", uuid)
				.uniqueResult()
		);
	}

	public static void markClaimed(UUID uuid, int seasonId) {
		try {
			DatabaseManager.instance().getSessionFactory().inTransaction(session -> {
				ChatGamesPendingReward entry = session.find(
					ChatGamesPendingReward.class, new ChatGamesPendingRewardId(uuid, seasonId));
				if (entry != null) {
					entry.claimed = true;
					session.merge(entry);
				}
			});
		} catch (Exception e) {
			Logger.error(e);
		}
	}

}
