package gg.mmorealms.module.essentials.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleCommand extends VelocityCommand {

	private String message;

	public SimpleCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	public SimpleCommand(CommonCommandManager commandManager, Command commandAnnotation, String message) {
		super(commandManager, commandAnnotation);
		this.message = message;
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		sendMessage(sender, message);
	}
}
