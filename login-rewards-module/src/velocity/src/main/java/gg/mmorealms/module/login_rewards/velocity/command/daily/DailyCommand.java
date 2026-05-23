package gg.mmorealms.module.login_rewards.velocity.command.daily;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;

import java.util.List;

@Command(aliases = {"daily"}, onlyFor = Command.OnlyFor.PLAYERS)
public class DailyCommand extends VelocityCommand {

	public DailyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		LoginrewardsVelocityModule.instance().getDailyManager().openDailyGUI(player);
	}
}
