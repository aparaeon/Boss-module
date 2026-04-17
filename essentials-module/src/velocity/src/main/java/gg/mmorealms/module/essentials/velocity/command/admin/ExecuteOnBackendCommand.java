package gg.mmorealms.module.essentials.velocity.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;

import java.util.List;

@Command(aliases = "execute_on_backend", arguments = {"target_player", "command..."})
public class ExecuteOnBackendCommand extends VelocityCommand {

	private @Inject ProxyServer proxy;
	private @Inject ServerManager serverManager;

	public ExecuteOnBackendCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String targetPlayer = arguments.getFirst();
		String command = String.join(" ", arguments.subList(1, arguments.size()));

		Player player = proxy.getPlayer(targetPlayer).orElse(null);

		if (player == null) {
			sendToRandomServer(sender, command);
			return;
		}

		ServerConnection connection = player.getCurrentServer().orElse(null);

		if (connection == null) {
			sendToRandomServer(sender, command);
			return;
		}

		EngineServer server = serverManager.getServer(connection.getServerInfo());

		if (server == null) {
			sendToRandomServer(sender, command);
			return;
		}

		CommandExecuteEvent.onBackend(server.getRedisID(), command).send();
		sendMessage(sender, "Executed"); // TODO Improve and config
	}

	private EngineServer getRandomServer() {
		List<EngineServer> servers = serverManager.getServers();

		if (servers.isEmpty()) {
			return null;
		}

		return RandomUtils.getRandom(servers);
	}

	private void sendToRandomServer(CommandSource sender, String command) {
		EngineServer engineServer = getRandomServer();

		if (engineServer == null) {
			Logger.error("Failed to execute command on backend. No server found.");
			return;
		}

		CommandExecuteEvent.onBackend(engineServer.getRedisID(), command).send();
		sendMessage(sender, "Executed"); // TODO Improve and config
	}
}
