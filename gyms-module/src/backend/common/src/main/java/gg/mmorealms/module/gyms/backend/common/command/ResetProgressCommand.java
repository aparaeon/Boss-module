package gg.mmorealms.module.gyms.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(aliases = "reset_progress", arguments = {"target"}, parent = GymsCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class ResetProgressCommand extends UserCommand {
	public ResetProgressCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> list) {
		GymsConfig config = GymsBackendModule.instance().getConfig();

		String target = list.getFirst();
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

		userGymRecord.delete();
		user.sendMessage(config.lang.resetProgressConfirmation.parse("target", target));
	}
}
