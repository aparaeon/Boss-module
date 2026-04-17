package gg.mmorealms.module.gyms.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.gyms.backend.common.gui.GymMainGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"gyms", "gym"}, onlyFor = Command.OnlyFor.PLAYERS)
public class GymsCommand extends UserCommand {
	public GymsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new GymMainGUI(user).open();
	}
}