package gg.mmorealms.module.chat.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;

import java.util.List;

@Command(aliases = {"velocitybroadcast", "velocity_broadcast", "broadcast"})
public class BroadcastCommand extends VelocityCommand {
	public BroadcastCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		ChatVelocityModule.instance().getMessageManager().sendGlobalMessage(String.join(" ", arguments));
	}
}
