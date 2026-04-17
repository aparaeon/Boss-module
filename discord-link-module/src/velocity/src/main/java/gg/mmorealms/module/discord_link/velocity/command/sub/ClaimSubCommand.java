package gg.mmorealms.module.discord_link.velocity.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.command.DiscordCommand;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.database.DiscordLinkedUser;

import java.util.List;

@Command(aliases = {"claim"}, onlyFor = Command.OnlyFor.PLAYERS, parent = DiscordCommand.class)
public class ClaimSubCommand extends VelocityCommand {

	public @Inject DiscordLinkConfig config;

	public ClaimSubCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		DiscordLinkedUser user = DiscordLinkedUser.getByPlayer(player);

		if (user == null) {
			sendMessage(player, config.lang.notLinked);
			return;
		}

		DiscordLinkVelocityModule.instance().getDiscordLinkerManager().award(user);
	}
}
