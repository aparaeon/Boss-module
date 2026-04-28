package gg.mmorealms.module.chat_games.velocity.command.chat_games.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;
import gg.mmorealms.module.chat_games.velocity.dto.ChatGamesSeasonWins;

import java.util.List;
import java.util.UUID;

@Command(aliases = {"removepoint"}, parent = ChatGamesAdminCommand.class, arguments = {"ign", "amount"})
public class RemovePointCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public RemovePointCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		if (arguments.size() < 2) {
			sender.sendMessage(miniMessageManager.parse(getUsage()));
			return;
		}

		String ign = arguments.get(0);
		long amount;
		try {
			amount = Long.parseLong(arguments.get(1));
		} catch (NumberFormatException e) {
			sender.sendMessage(miniMessageManager.parse(
				ChatGamesVelocityModule.instance().getConfig().lang.adminInvalidAmount.parse()
			));
			return;
		}

		if (amount <= 0) {
			sender.sendMessage(miniMessageManager.parse(
				ChatGamesVelocityModule.instance().getConfig().lang.adminInvalidAmount.parse()
			));
			return;
		}

		long finalAmount = amount;
		String senderName = sender instanceof Player ? ((Player) sender).getUsername() : "Console";
		ScheduleUtils.runTaskAsync(() -> {
			UUID uuid = MojangUtils.getUUID(ign);
			if (uuid == null) {
				sender.sendMessage(miniMessageManager.parse(
					ChatGamesVelocityModule.instance().getConfig().lang.adminPlayerNotFound.parse("ign", ign)
				));
				return;
			}

			int seasonId = ChatGamesVelocityModule.instance().getSeasonManager().getCurrentSeasonId();
			ChatGamesSeasonWins.adjustWins(uuid, seasonId, -finalAmount);
			ChatGamesVelocityModule.instance().getLeaderboardManager().refresh();

			Logger.info("[ChatGames] Removed " + finalAmount + " point(s) from " + ign + " via removepoint command (executor: " + senderName + ")");

			sender.sendMessage(miniMessageManager.parse(
				ChatGamesVelocityModule.instance().getConfig().lang.adminPointRemoved
					.parse("ign", ign)
					.parse("amount", finalAmount)
			));
		});
	}

}
