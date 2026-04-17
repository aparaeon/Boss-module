package gg.mmorealms.module.analytics.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.utils.AccessUtils;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"user_stats", "stats"}, arguments = "target", onlyFor = Command.OnlyFor.PLAYERS)
public class UserStatsCommand extends VelocityCommand {
	public UserStatsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return recommendPlayersList();
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetUsernameOrUUID = arguments.getFirst();

		UserStats stats = AnalyticsVelocityModule.instance().getByUUIDOrUsername(
				targetUsernameOrUUID,
				UserStats::getByPlayer,
				UserStats::getByUUID,
				(ignored) -> null
		);

		if (stats == null) {
			sendMessage(player, "User not found"); // TODO Config
			return;
		}

		// TODO Config
		MessageBuilderList sensitivieTemplate = new MessageBuilderList(List.of(
				"<aqua>Purchases (total): <white>${purchases_total}<reset>",
				"<aqua>Purchases (this month): <white>${purchases_this_month}<reset>",
				"<aqua>Purchases (last month): <white>${purchases_last_month}<reset>"
		));

		// TODO Config
		MessageBuilderList template = new MessageBuilderList(List.of(
				"",
				"<gold><b>{user}'s Statistics<reset>",
				"",
				"<aqua>First Join Domain: <white>{first_join_domain}<reset>",
				"<aqua>Last Time Online: <white>{last_time_online}<reset> {afk_tag}",
				"<aqua>Total Online Time: <white>{online_time}<reset>",
				"<aqua>Total AFK Time: <white>{afk_time}<reset>",
				"{sensitive}",

				""
		))
				.parse("user", AnalyticsVelocityModule.instance().getDisplayName(targetUsernameOrUUID))
				.parse("first_join_domain", stats.getFirstJoinDomain())
				.parse("afk_tag", AnalyticsVelocityModule.instance().getAfkManager().isAfk(stats.getUuid()) ? "<red>(AFK)<reset>" : "")
				.parse("last_time_online", stats.getLastTimeOnlineFormatted())
				.parse("online_time", stats.getOnlineTimeFormatted())
				.parse("afk_time", stats.getAfkTimeFormatted())
				.parse("sensitive",
						AccessUtils.hasAccessToSensitiveInformation(player.getUniqueId()) ?
								sensitivieTemplate :
								""
				)
				.parse("purchases_total", NumberUtils.formatNumberWithDecimalPlaces(stats.getPurchasesTotal(0, System.currentTimeMillis()), 2))
				.parse("purchases_this_month", NumberUtils.formatNumberWithDecimalPlaces(stats.getPurchaseTotalMonthly(0), 2))
				.parse("purchases_last_month", NumberUtils.formatNumberWithDecimalPlaces(stats.getPurchaseTotalMonthly(1), 2));

		sendMessage(player, template);
	}


}
