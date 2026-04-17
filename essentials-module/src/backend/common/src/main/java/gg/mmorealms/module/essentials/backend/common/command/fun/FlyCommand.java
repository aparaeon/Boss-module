package gg.mmorealms.module.essentials.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.dto.user_settings.FlySetting;
import gg.mmorealms.module.user_data.backend.common.database.user_settings.BackendUserSettings;
import gg.mmorealms.module.user_data.backend.common.database.user_settings.IBackendUserSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"fly"}, onlyFor = Command.OnlyFor.PLAYERS)
public class FlyCommand extends UserCommand {

	public FlyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		BackendUserSettings userSettings = IBackendUserSettings.getByUser(user);
		FlySetting flySetting = userSettings.get(FlySetting.class);
		flySetting.value = !flySetting.value;

		flySetting.apply(user.getPlayer());
		user.sendMessage(new MessageBuilder("Fly has been {status}")
				.parse("status", flySetting.value ? "enabled" : "disabled")
		); // TODO Config
	}
}
