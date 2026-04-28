package gg.mmorealms.module.chat_games.velocity.command.chat_games;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.chat_games.common.dto.ChatGamesPendingReward;
import gg.mmorealms.module.chat_games.common.dto.event.OpenRewardClaimEvent;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;
import gg.mmorealms.module.chat_games.velocity.config.ChatGamesConfig;
import gg.mmorealms.module.chat_games.velocity.config.SeasonReward;
import gg.mmorealms.module.chat_games.velocity.manager.SeasonManager;

import java.util.List;

@Command(aliases = {"claim"}, parent = ChatGamesCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class ClaimRewardCommand extends VelocityCommand {

	private @Inject SeasonManager seasonManager;

	public ClaimRewardCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		ChatGamesConfig config = ChatGamesVelocityModule.instance().getConfig();

		ChatGamesPendingReward pending = ChatGamesPendingReward.getUnclaimed(player.getUniqueId());
		if (pending == null) {
			sendMessage(player, config.lang.noRewardToClaim.parse());
			return;
		}

		SeasonReward seasonReward = seasonManager.getRewardForPlacement(pending.getPlacement());
		if (seasonReward == null) {
			sendMessage(player, config.lang.noRewardToClaim.parse());
			return;
		}

		long unclaimed = ChatGamesPendingReward.countUnclaimed(player.getUniqueId());
		if (unclaimed > 1) {
			sendMessage(player, config.lang.multipleRewardsPending
				.parse("count", String.valueOf(unclaimed))
				.parse());
		}

		String username = MojangUtils.getUsernameOrUUID(player.getUniqueId());
		List<String> commands = seasonReward.getCommands()
			.parse("player", username)
			.parse("placement", String.valueOf(pending.getPlacement()))
			.parse("wins", String.valueOf(pending.getWins()))
			.parse();

		new OpenRewardClaimEvent(
			player.getUniqueId(),
			pending.getSeasonId(),
			pending.getPlacement(),
			pending.getWins(),
			seasonReward.getRequiredSlots(),
			seasonReward.getDisplayItems(),
			commands,
			seasonReward.getRewardLore()
		).send();
	}

}
