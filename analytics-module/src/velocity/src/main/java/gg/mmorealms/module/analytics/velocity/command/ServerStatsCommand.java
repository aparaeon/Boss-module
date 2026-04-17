package gg.mmorealms.module.analytics.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

@Command(aliases = {"server_stats"}, arguments = {"timeframe", "bounce_threshold"})
public class ServerStatsCommand extends VelocityCommand {
	public ServerStatsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String timeframeString = arguments.get(0);
		String bounceRateThresholdString = arguments.get(1);

		Time timeframe = Time.parse(timeframeString);
		Time bounceThreshold = Time.parse(bounceRateThresholdString);

		if (timeframe == null || bounceThreshold == null) {
			sendMessage(sender, "Invalid timeframe or bounce rate threshold format. Use '1h', '1d', etc."); // TODO Config
			return;
		}

		long timeframeMillis = timeframe.toMilliseconds();
		long bounceThresholdMillis = bounceThreshold.toMilliseconds();

		long timeframeTimestamp = System.currentTimeMillis() - timeframeMillis;

		sendMessage(sender, "Querying the database. This might take a while..."); // TODO Config

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			long totalPlayers = session.createQuery("SELECT COUNT(*) FROM user_stats", Long.class).getSingleResult();
			long newPlayerInTimeframeCount = session.createQuery("SELECT COUNT(*) from user_stats where (firstJoinTimestamp > :timestamp)", Long.class)
					.setParameter("timestamp", timeframeTimestamp)
					.getSingleResult();
			long bouncedPlayerCount = session.createQuery("SELECT COUNT(*) from user_stats where (firstJoinTimestamp > :timestamp) AND (onlineTime < :bounce_threshold)", Long.class)
					.setParameter("timestamp", timeframeTimestamp)
					.setParameter("bounce_threshold", bounceThresholdMillis)
					.getSingleResult();
			long deadPlayerCount = session.createQuery("SELECT COUNT(*) from user_stats where (firstJoinTimestamp <= :timestamp) AND (lastOnlineTimestamp <= :timestamp)", Long.class)
					.setParameter("timestamp", timeframeTimestamp)
					.getSingleResult();
			long activePlayerCount = session.createQuery("SELECT COUNT(*) from user_stats where (firstJoinTimestamp <= :timestamp) AND (lastOnlineTimestamp > :timestamp)", Long.class)
					.setParameter("timestamp", timeframeTimestamp)
					.getSingleResult();

			HashMap<String, Long> domainUserCounts = new HashMap<>();

			session.createQuery("""
							SELECT
							    firstJoinDomain,
							    COUNT(uuid) AS user_count
							FROM
							    user_stats
							WHERE
							    firstJoinTimestamp > :timestamp
							GROUP BY
							    firstJoinDomain
							""", Object[].class)
					.setParameter("timestamp", timeframeTimestamp)
					.getResultList()
					.forEach(result -> {
						if (result.length != 2) {
							Logger.error("Invalid result length from query: " + result.length);
							return;
						}

						if (result[0] == null || result[1] == null) {
							Logger.error("Null value found in query result: " + result[0] + ", " + result[1]);
							return;
						}

						domainUserCounts.put((String) result[0], (Long) result[1]);
					});

			// TODO Config
			MessageBuilderList template = new MessageBuilderList(List.of(
					"",
					"<gold><b>Server Statistics for the last {timeframe}<reset>",
					"",
					"<aqua>Total Players: <white>{total_players}<reset>",
					"<aqua>Total New Players: <white>{new_players}<reset>",
					"<aqua>Bounced New Players: <white>{bounced_players}<reset>",
					"<aqua>Active Players: <white>{active_players}<reset>",
					"<aqua>Dead Players: <white>{dead_players}<reset>",
					"",
					"<gold><b>Domain Stats<reset>",
					"{domain_entries}<reset>",
					""
			))
					.parse("total_players", NumberUtils.formatNumberWithUnits(totalPlayers))
					.parse("new_players", NumberUtils.formatNumberWithUnits(newPlayerInTimeframeCount))
					.parse("bounced_players", NumberUtils.formatNumberWithUnits(bouncedPlayerCount))
					.parse("active_players", NumberUtils.formatNumberWithUnits(activePlayerCount))
					.parse("dead_players", NumberUtils.formatNumberWithUnits(deadPlayerCount))
					.parse("timeframe", timeframe.toString())
					.parse("domain_entries", domainUserCounts.entrySet().stream()
							.map(entry -> new MessageBuilder("<white> - <aqua>{domain}: <white>{count}")
									.parse("domain", entry.getKey())
									.parse("count", NumberUtils.formatNumberWithUnits(entry.getValue()))
									.parse())
							.reduce((a, b) -> a + "\n" + b)
							.orElse("<gray>No domains found"));

			sendMessage(sender, template);

		}

	}
}
