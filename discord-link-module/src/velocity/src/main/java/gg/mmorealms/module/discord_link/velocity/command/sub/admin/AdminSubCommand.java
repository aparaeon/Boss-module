package gg.mmorealms.module.discord_link.velocity.command.sub.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.discord_link.velocity.command.DiscordCommand;

import java.util.List;

@Command(aliases = {"admin"}, onlyFor = Command.OnlyFor.PLAYERS, parent = DiscordCommand.class)
public class AdminSubCommand extends VelocityCommand {

	public AdminSubCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {

	}
}
