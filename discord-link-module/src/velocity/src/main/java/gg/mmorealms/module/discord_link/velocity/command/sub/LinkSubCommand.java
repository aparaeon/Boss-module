package gg.mmorealms.module.discord_link.velocity.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.discord_link.velocity.command.DiscordCommand;
import gg.mmorealms.module.discord_link.velocity.command.LinkCommand;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;

import java.util.List;

@Command(aliases = {"link"}, onlyFor = Command.OnlyFor.PLAYERS, parent = DiscordCommand.class)
public class LinkSubCommand extends VelocityCommand {

	public @Inject VelocityCommandManager commandManager;

	public LinkSubCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		commandManager.getCommand(LinkCommand.class).execute(player, arguments);
	}

}
