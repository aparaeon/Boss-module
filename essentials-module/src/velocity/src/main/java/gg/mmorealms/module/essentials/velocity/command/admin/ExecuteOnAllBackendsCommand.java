package gg.mmorealms.module.essentials.velocity.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;

import java.util.List;

@Command(aliases = "execute_on_all_backends", arguments = {"command..."})
public class ExecuteOnAllBackendsCommand extends VelocityCommand {

	private @Inject ServerManager serverManager;

	public ExecuteOnAllBackendsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String command = String.join(" ", arguments);

		for (EngineServer server : serverManager.getServers()) {
			CommandExecuteEvent.onBackend(server.getRedisID(), command).send();
		}

		sendMessage(sender, "Executed"); // TODO Improve and config
	}
}
