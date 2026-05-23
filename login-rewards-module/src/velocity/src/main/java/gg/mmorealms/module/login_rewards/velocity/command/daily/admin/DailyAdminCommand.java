package gg.mmorealms.module.login_rewards.velocity.command.daily.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.login_rewards.velocity.command.daily.DailyCommand;

import java.util.List;

@Command(aliases = {"admin"}, parent = DailyCommand.class)
public class DailyAdminCommand extends VelocityCommand {

	public DailyAdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		sendMessage(sender, getUsage());
	}
}
