package gg.mmorealms.module.discord_link.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.exception.AlreadyLinkedException;
import gg.mmorealms.module.discord_link.velocity.manager.DiscordLinkerManager;

import java.util.List;

@Command(aliases = {"link"}, onlyFor = Command.OnlyFor.PLAYERS)
public class LinkCommand extends VelocityCommand {

	public @Inject DiscordLinkConfig config;

	public LinkCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		try {
			DiscordLinkerManager.Link link = DiscordLinkVelocityModule.instance().getDiscordLinkerManager().initializeLink(player.getUniqueId());
			sendMessage(player, config.lang.link
					.parse("guild_id", config.guildID)
					.parse("channel_id", config.linkChannelID)
					.parse("code", link.getCode())
			);
		} catch (AlreadyLinkedException exception) {
			sendMessage(player, exception.getMessage());
		}
	}

}
