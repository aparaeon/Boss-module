package gg.mmorealms.module.homes.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.homes.backend.common.gui.HomesGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"home", "homes"}, onlyFor = Command.OnlyFor.PLAYERS)
public class HomesCommand extends UserCommand {
	public HomesCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new HomesGUI(user).open();
	}
}
