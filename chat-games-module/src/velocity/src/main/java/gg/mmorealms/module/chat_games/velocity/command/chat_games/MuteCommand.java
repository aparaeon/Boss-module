package gg.mmorealms.module.chat_games.velocity.command.chat_games;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;
import gg.mmorealms.module.chat_games.velocity.dto.user_settings.ChatGamesNotificationSetting;
import gg.mmorealms.module.user_data.velocity.database.VelocityUserSettings;

import java.util.List;

@Command(aliases = {"mute"}, parent = ChatGamesCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class MuteCommand extends VelocityCommand {

	public MuteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		VelocityUserSettings settings = VelocityUserSettings.getByUUID(player.getUniqueId());
		if (settings == null) {
			return;
		}
		ChatGamesNotificationSetting setting = settings.get(ChatGamesNotificationSetting.class);
		setting.toggle();
		try {
			settings.save();
		} catch (DatabaseSaveException e) {
			Logger.error(e);
		}

		String message = setting.isEnabled()
			? ChatGamesVelocityModule.instance().getConfig().lang.toggledOn.parse()
			: ChatGamesVelocityModule.instance().getConfig().lang.toggledOff.parse();

		sendMessage(player, message);
	}

}
