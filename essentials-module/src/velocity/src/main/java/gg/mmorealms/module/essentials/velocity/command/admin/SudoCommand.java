package gg.mmorealms.module.essentials.velocity.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;

import java.util.List;

@Command(aliases = "sudo", arguments = {"target", "command..."})
public class SudoCommand extends VelocityCommand {

	private @Inject ProxyServer proxy;

	public SudoCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String targetUsername = arguments.getFirst();
		String command = String.join(" ", arguments.subList(1, arguments.size()));

		Player targetPlayer = proxy.getPlayer(targetUsername).orElse(null);

		if (targetPlayer == null) {
			sendMessage(sender, "Player not found");
			return;
		}

		ServerConnection serverConnection = targetPlayer.getCurrentServer().orElse(null);

		if (serverConnection == null) {
			sendMessage(sender, "Player is not connected to a server");
			return;
		}

		String targetServer = serverConnection.getServerInfo().getName();

		CommandExecuteEvent.onBackend(targetServer, command, targetUsername).send();
		sendMessage(sender, "Executed"); // TODO Improve and config
	}
}
