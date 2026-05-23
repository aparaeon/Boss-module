package gg.mmorealms.module.login_rewards.velocity.command.daily.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;
import gg.mmorealms.module.login_rewards.velocity.config.LoginRewardsModuleConfig;
import gg.mmorealms.module.login_rewards.velocity.manager.DailyManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(aliases = {"check"}, arguments = {"player"}, parent = DailyAdminCommand.class)
public class DailyAdminCheckCommand extends VelocityCommand {

	public DailyAdminCheckCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String target = arguments.getFirst();
		ScheduleUtils.runTaskAsync(() -> sendCheck(sender, target));
	}

	private void sendCheck(CommandSource sender, String target) {
		LoginrewardsVelocityModule module = LoginrewardsVelocityModule.instance();
		UUID uuid = module.getUUID(target);
		LoginRewardsModuleConfig config = module.getConfig();

		if (uuid == null) {
			sendMessage(sender, config.lang.playerNotFound
				.parse("player", target)
				.parse()
			);
			return;
		}

		DailyManager.DailyStatus status = module.getDailyManager().getStatus(uuid);
		long nowMs = System.currentTimeMillis();

		String previousStreakHint = status.streakBroken() && status.previousStreak() > 0
			? " (was " + status.previousStreak() + ")"
			: "";
		String onlineLabel = module.getProxy().getPlayer(uuid).isPresent() ? "<green>online" : "<gray>offline";
		boolean neverClaimed = status.daily().getLastClaimTimestamp() == 0L;
		String lastClaimAge = neverClaimed ? "never" : DailyManager.formatDuration(nowMs - status.daily().getLastClaimTimestamp());
		String availableIn = DailyManager.formatAvailableIn(status, nowMs);
		String claimWindow = neverClaimed ? "—" : DailyManager.formatCountdown(status.claimExpiresAtMs() - nowMs);
		String todayPlaytime = status.playtimeKnown() ? DailyManager.formatDuration(status.todayPlaytimeMs()) : "—";
		String requiredPlaytime = status.playtimeKnown() ? DailyManager.formatDuration(status.requiredPlaytimeMs()) : "—";

		sendMessage(sender, config.lang.checkInfo
			.parse("player", MojangUtils.getUsernameOrUUID(uuid))
			.parse("online", onlineLabel)
			.parse("streak", status.daily().getStreak())
			.parse("previous_streak_hint", previousStreakHint)
			.parse("today", todayPlaytime)
			.parse("required", requiredPlaytime)
			.parse("day", status.claimDay())
			.parse("last_claim_age", lastClaimAge)
			.parse("available_in", availableIn)
			.parse("claim_window", claimWindow)
			.parse()
		);
	}

}
