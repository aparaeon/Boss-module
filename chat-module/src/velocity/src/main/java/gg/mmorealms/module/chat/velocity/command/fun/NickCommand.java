package gg.mmorealms.module.chat.velocity.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.dto.user_settings.NicknameSetting;
import gg.mmorealms.module.chat.velocity.exceptions.InvalidNicknameException;
import gg.mmorealms.module.user_data.velocity.database.VelocityUserSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"nick", "nickname"}, onlyFor = Command.OnlyFor.PLAYERS)
public class NickCommand extends VelocityCommand {
	public NickCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return List.of("reset");
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String nickname = arguments.get(0);

		if (nickname.equals("reset")) {
			nickname = "";
		}

		VelocityUserSettings userSettings = VelocityUserSettings.getByUUID(player.getUniqueId());
		NicknameSetting nicknameSetting = userSettings.get(NicknameSetting.class);
		try {
			nickname = nicknameSetting.setValue(nickname);
		} catch (InvalidNicknameException exception) {
			sendMessage(player, exception.getMessage());
			return;
		}

		sendMessage(player, new MessageBuilder("Your nickname has been {action}")
				.parse("action", nickname.isEmpty() ?
						"reset to default" :
						("set to " + nickname)
				)
		); // TODO Config
	}
}
