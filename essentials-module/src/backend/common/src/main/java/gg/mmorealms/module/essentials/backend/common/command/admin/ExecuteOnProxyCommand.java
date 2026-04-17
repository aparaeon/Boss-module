package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "execute_on_proxy", arguments = {"command..."})
public class ExecuteOnProxyCommand extends BackendCommand {

	public ExecuteOnProxyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String command = String.join(" ", arguments);

		CommandExecuteEvent.onProxy(command).send();
		sendMessage(sender, "Executed"); // TODO Improve and config
	}
}
