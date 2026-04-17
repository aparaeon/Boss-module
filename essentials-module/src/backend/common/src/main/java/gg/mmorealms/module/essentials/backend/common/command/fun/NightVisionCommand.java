package gg.mmorealms.module.essentials.backend.common.command.fun;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.essentials.backend.common.dto.user_settings.NightVisionSetting;
import gg.mmorealms.module.user_data.backend.common.database.user_settings.IBackendUserSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"night_vision", "nv", "nightvision"}, onlyFor = Command.OnlyFor.PLAYERS)
public class NightVisionCommand extends UserCommand {

	public NightVisionCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		NightVisionSetting nightVisionSetting = IBackendUserSettings.getByUser(user).get(NightVisionSetting.class);
		nightVisionSetting.value = !nightVisionSetting.value;

		nightVisionSetting.apply(user.getPlayer());
		user.sendMessage(new MessageBuilder("Night vision has been {status}")
				.parse("status", nightVisionSetting.value ? "enabled" : "disabled")
		); // TODO Config
	}
}
