package gg.mmorealms.module.login_rewards.velocity.command.daily;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;

import java.util.List;

@Command(aliases = {"playtime"}, parent = DailyCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class DailyPlaytimeCommand extends VelocityCommand {

	public DailyPlaytimeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		sendMessage(player, LoginrewardsVelocityModule.instance().getConfig().lang.playtimeInfo
			.parse("playtime", UserStats.getByPlayer(player).getOnlineTimeFormatted())
			.parse()
		);
	}
}
