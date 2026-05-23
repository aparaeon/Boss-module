package gg.mmorealms.module.login_rewards.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.core.common.dto.event.UserFullyLoadedEvent;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.login_rewards.common.dto.event.ClaimDailyRewardEvent;
import gg.mmorealms.module.login_rewards.velocity.LoginrewardsVelocityModule;
import gg.mmorealms.module.login_rewards.velocity.config.LoginRewardsModuleConfig;

public class LoginRewardsListener {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	@EventHandler
	public void onUserFullyLoaded(UserFullyLoadedEvent event) {
		LoginrewardsVelocityModule module = LoginrewardsVelocityModule.instance();
		LoginRewardsModuleConfig config = module.getConfig();

		Player player = module.getProxy().getPlayer(event.getUuid()).orElse(null);
		if (player == null) {
			Logger.warn("Skipped daily handling for " + event.getUuid() + ": player is not online on this proxy.");
			return;
		}

		DailyManager.DailyStatus status = module.getDailyManager().getStatus(event.getUuid());

		if (status.streakBroken() && status.previousStreak() > 0) {
			player.sendMessage(miniMessageManager.parse(config.lang.streakBroken.parse()));
		}

		if (!status.shouldAutoOpen()) {
			return;
		}

		if (config.autoOpenDailyGui && module.getDailyManager().markAutoOpened(event.getUuid())) {
			module.getDailyManager().openDailyGUI(player);
			return;
		}

		if (!config.autoOpenDailyGui) {
			player.sendMessage(miniMessageManager.parse(config.lang.dailyReady.parse()));
		}
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		LoginrewardsVelocityModule.instance().getDailyManager().clearAutoOpened(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onClaimDailyReward(ClaimDailyRewardEvent event) {
		Logger.debug("Received ClaimDailyRewardEvent for " + event.getPlayerUuid() + " day " + event.getClaimDay() + ".");

		LoginrewardsVelocityModule module = LoginrewardsVelocityModule.instance();
		DailyManager dailyManager = module.getDailyManager();
		LoginRewardsModuleConfig config = module.getConfig();

		Player player = module.getProxy().getPlayer(event.getPlayerUuid()).orElse(null);
		if (player == null) {
			Logger.warn("Dropped daily claim for " + event.getPlayerUuid() + ": player is not online on this proxy.");
			return;
		}

		EngineServer rewardServer = dailyManager.getRewardDispatchServer(player);
		if (rewardServer == null) {
			player.sendMessage(miniMessageManager.parse(config.lang.claimFailed.parse()));
			Logger.warn("Daily claim for " + event.getPlayerUuid()
				+ " was not finalized because no dispatch server was available.");
			return;
		}

		DailyManager.ClaimResult result = dailyManager.markClaimedFromBackend(event.getPlayerUuid(), event.getClaimDay());
		Logger.debug("Claim result for " + event.getPlayerUuid() + " day " + event.getClaimDay() + ": " + result.type()
			+ " (finalStreak=" + result.finalStreak() + ", streakBroken=" + result.streakBroken() + ").");

		if (result.type() == DailyManager.ClaimResult.Type.CLAIM_FAILED) {
			player.sendMessage(miniMessageManager.parse(config.lang.claimFailed.parse()));
			Logger.warn("Failed to finalize daily reward claim for " + event.getPlayerUuid()
				+ " day " + event.getClaimDay() + ".");
			return;
		}

		if (result.type() == DailyManager.ClaimResult.Type.NOT_ENOUGH_PLAYTIME) {
			player.sendMessage(miniMessageManager.parse(config.lang.notEnoughPlaytime
				.parse("needed", DailyManager.formatDuration(result.remainingPlaytimeMs()))
				.parse("day", result.claimDay())
				.parse()));
			return;
		}

		if (result.type() == DailyManager.ClaimResult.Type.ALREADY_CLAIMED) {
			player.sendMessage(miniMessageManager.parse(config.lang.alreadyClaimed.parse()));
			return;
		}

		if (result.streakBroken()) {
			player.sendMessage(miniMessageManager.parse(config.lang.streakBroken.parse()));
		}

		dailyManager.dispatchRewardCommands(player, event.getClaimDay(), rewardServer);

		player.sendMessage(miniMessageManager.parse(config.lang.dailyClaimed
			.parse("day", result.finalStreak())
			.parse("streak", result.finalStreak())
			.parse()));
		Logger.debug("Daily claim complete for " + event.getPlayerUuid() + " day " + event.getClaimDay()
			+ ", streak now " + result.finalStreak() + ".");
	}
}
