package gg.mmorealms.module.analytics.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Command(aliases = {"alts"}, arguments = {"target"})
public class AltsCommand extends VelocityCommand {
	public AltsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String targetUsernameOrUUID = arguments.getFirst();

		UserStats stats = AnalyticsVelocityModule.instance().getByUUIDOrUsername(
				targetUsernameOrUUID,
				UserStats::getByPlayer,
				UserStats::getByUUID,
				(ignored) -> null
		);

		String ip = stats.getIp();

		if (ip == null || ip.isEmpty()) {
			sendMessage(sender, new MessageBuilder("No IP found for user {user}")
					.parse("user", targetUsernameOrUUID)
			);
			return;
		}

		List<UserStats> alts = UserStats.getByIP(ip);

		MessageBuilderList template = new MessageBuilderList(List.of(
				"<gold><b>Alts for {user} ({ip}):<reset>",
				"",
				"{entry}"
		))
				.parse("user", targetUsernameOrUUID)
				.parse("entry",
						alts.stream()
								.map(UserStats::getIdentifier)
								.map(MojangUtils::getUsername)
								.map(username -> new MessageBuilder("<aqua>- <white>{username}<reset>")
										.parse("username", username)
										.parse()
								)
								.collect(Collectors.joining("\n"))
				)
				.parse("ip", ip);

		sendMessage(sender, template);
	}
}
