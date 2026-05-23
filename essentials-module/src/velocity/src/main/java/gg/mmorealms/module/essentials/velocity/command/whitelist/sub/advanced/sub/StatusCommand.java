package gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.AdvancedCommand;
import gg.mmorealms.module.essentials.velocity.manager.WhitelistManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Command(aliases = {"status"}, parent = AdvancedCommand.class)
@Getter
@Setter
public class StatusCommand extends VelocityCommand {

	// TODO Config
	private final static MessageBuilderList template = new MessageBuilderList(List.of(
			"Whitelist state:",
			"",
			"Global status: {status}<reset>",
			"",
			"Individual servers:",
			"{whitelisted-servers}",
			"",
			"Server Types:",
			"{whitelisted-server-types}",
			"",
			"Bypass players:",
			"{bypass-players}",
			""
	));

	// TODO Config
	private final static MessageBuilder whitelistedServerEntryTemplate = new MessageBuilder("  - {server-id}");

	// TODO Config
	private final static MessageBuilder whitelistedServerTypeEntryTemplate = new MessageBuilder("  - {server-type}");

	// TODO Config
	private final static MessageBuilder bypassPlayerEntryTemplate = new MessageBuilder("  - {username}");


	private @Inject WhitelistManager whitelistManager;

	public StatusCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {

		sendMessage(
				sender,
				template
						.parse("status", whitelistManager.getState().isGlobalEnabled() ? "<red>enabled" : "<green>disabled")
						.parse(
								"whitelisted-servers",
								whitelistManager.getState().getWhitelistedServers().stream()
										.map(serverId ->
												whitelistedServerEntryTemplate
														.parse("server-id", serverId)
														.parse()
										)
										.toList()
						)
						.parse(
								"whitelisted-server-types",
								whitelistManager.getState().getWhitelistedServerTypes().stream()
										.map(serverType ->
												whitelistedServerTypeEntryTemplate
														.parse("server-type", serverType)
														.parse()
										)
										.toList()
						)
						.parse(
								"bypass-players",
								whitelistManager.getState().getBypassPlayers().stream()
										.map(player ->
												bypassPlayerEntryTemplate
														.parse("username", player)
														.parse()
										)
										.toList()
						)
		);


	}


}
