package gg.mmorealms.module.chat.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"broadcastserver", "broadcast_server"}, arguments = {"server", "message..."})
public class BroadcastServerCommand extends VelocityCommand {

	private @Inject ServerManager serverManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	public BroadcastServerCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String serverID = arguments.getFirst();
		String message = String.join(" ", arguments.subList(1, arguments.size()));

		EngineServer server = serverManager.getServer(serverID);
		if (server == null) {
			sendMessage(sender, "<red>Server not found: <white>" + serverID);
			return;
		}

		RegisteredServer registeredServer = server.getProxyServer();
		if (registeredServer == null) {
			sendMessage(sender, "<red>Server is not connected: <white>" + serverID);
			return;
		}

		registeredServer.sendMessage(miniMessageManager.parse(message));
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		if (arguments.size() == 1) {
			return serverManager.getServers().stream()
					.filter(server -> server.getProxyServer() != null)
					.map(EngineServer::getServerID)
					.toList();
		}

		return new ArrayList<>();
	}

}
