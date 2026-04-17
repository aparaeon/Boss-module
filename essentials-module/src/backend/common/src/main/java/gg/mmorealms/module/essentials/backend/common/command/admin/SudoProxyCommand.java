package gg.mmorealms.module.essentials.backend.common.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.essentials.common.dto.event.CommandExecuteEvent;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = "sudo_proxy", arguments = {"target", "command..."})
public class SudoProxyCommand extends BackendCommand {

	public SudoProxyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsername = arguments.getFirst();
		String command = String.join(" ", arguments.subList(1, arguments.size()));

		CommandExecuteEvent.onProxy(command, targetUsername).sendAndGet();
		sendMessage(sender, "Executed"); // TODO Improve and config
	}
}
