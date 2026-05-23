package gg.mmorealms.module.login_rewards.velocity.command.daily;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;
import gg.mmorealms.module.login_rewards.velocity.config.LoginRewardsModuleConfig;
import gg.mmorealms.module.login_rewards.velocity.manager.DailyManager;

import java.util.List;

@Command(aliases = {"streak"}, parent = DailyCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class DailyStreakCommand extends VelocityCommand {

	public DailyStreakCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		DailyManager.DailyStatus status = LoginrewardsVelocityModule.instance().getDailyManager().getStatus(player.getUniqueId());
		LoginRewardsModuleConfig config = LoginrewardsVelocityModule.instance().getConfig();
		long nowMs = System.currentTimeMillis();
		boolean neverClaimed = status.daily().getLastClaimTimestamp() == 0L;
		String availableIn = DailyManager.formatAvailableIn(status, nowMs);
		String claimWindow = neverClaimed ? "—" : DailyManager.formatCountdown(status.claimExpiresAtMs() - nowMs);
		int nextDay = status.alreadyClaimed()
			? status.daily().getStreak() + 1
			: status.claimDay();
		MessageBuilder message = status.alreadyClaimed()
			? config.lang.streakClaimedInfo
			: config.lang.streakInfo;

		sendMessage(player, message
			.parse("streak", status.daily().getStreak())
			.parse("today", status.alreadyClaimed() ? "claimed" : DailyManager.formatDuration(status.todayPlaytimeMs()))
			.parse("required", DailyManager.formatDuration(status.requiredPlaytimeMs()))
			.parse("day", nextDay)
			.parse("available_in", availableIn)
			.parse("claim_window", claimWindow)
			.parse()
		);
	}
}
