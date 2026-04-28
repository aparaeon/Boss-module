package gg.mmorealms.module.chat_games.velocity.command.chat_games;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat_games.common.dto.LeaderboardEntry;
import gg.mmorealms.module.chat_games.common.dto.event.OpenLeaderboardEvent;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;
import gg.mmorealms.module.chat_games.velocity.config.SeasonReward;
import gg.mmorealms.module.chat_games.velocity.manager.SeasonManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command(aliases = {"leaderboard", "lb"}, parent = ChatGamesCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class LeaderboardCommand extends VelocityCommand {

	public LeaderboardCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		List<LeaderboardEntry> overall = new ArrayList<>(ChatGamesVelocityModule.instance().getLeaderboardManager().getOverallLeaderboard());
		List<LeaderboardEntry> season = new ArrayList<>(ChatGamesVelocityModule.instance().getLeaderboardManager().getSeasonLeaderboard());
		new OpenLeaderboardEvent(player.getUniqueId(), overall, season, true, buildSeasonRewardLore(ChatGamesVelocityModule.instance().getSeasonManager())).send();
	}

	static Map<Integer, List<String>> buildSeasonRewardLore(SeasonManager seasonManager) {
		Map<Integer, List<String>> map = new HashMap<>();
		for (int placement = 1; placement <= 3; placement++) {
			SeasonReward reward = seasonManager.getRewardForPlacement(placement);
			if (reward == null || reward.getRewardLore().isEmpty()) {
				continue;
			}

			map.put(placement, List.copyOf(reward.getRewardLore()));
		}
		return map;
	}

}
