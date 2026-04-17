package gg.mmorealms.module.core.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.core.velocity.CoreVelocityModule;

import java.util.List;

@Command(aliases = {"kick_all"})
public class KickAllCommand extends VelocityCommand {

	public KickAllCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		sendMessage(sender, "Kicking all players...");
		for (Player player : CoreVelocityModule.instance().getProxy().getAllPlayers()) {
			player.disconnect(CoreVelocityModule.instance().getMiniMessageManager().parse("<red>You have been kicked by an admin!"));
		}
		sendMessage(sender, "All players have been kicked.");
	}
}
