package gg.mmorealms.module.gyms.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.gyms.backend.common.gui.BadgesGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"badges", "badge"}, onlyFor = Command.OnlyFor.PLAYERS)
public class BadgesCommand extends UserCommand {
	public BadgesCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new BadgesGUI(user).open();
	}
}