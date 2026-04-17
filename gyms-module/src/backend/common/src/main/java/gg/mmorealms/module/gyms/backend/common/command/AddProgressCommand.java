package gg.mmorealms.module.gyms.backend.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command(aliases = "add_progress", arguments = {"target", "gym_name..."}, parent = GymsCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class AddProgressCommand extends UserCommand {
	public AddProgressCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("target")) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> list) {
		GymsConfig config = GymsBackendModule.instance().getConfig();

		String target = list.get(0);
		String gymId = list.get(1);
		IUserGymRecord userGymRecord = IUserGymRecord.get(target);

		if (userGymRecord == null) {
			UUID targetUuid;
			try {
				targetUuid = UUID.fromString(target);
			} catch (Exception e) {
				user.sendMessage(config.lang.invalidTargetArgument);
				return;
			}

			userGymRecord = IUserGymRecord.get(targetUuid);
		}

		userGymRecord.logWin(gymId);
		user.sendMessage(config.lang.addProgressConfirmation
				.parse("target", target)
				.parse("gym", gymId)
		);
	}
}
