package gg.mmorealms.module.realms.backend.common.command;


import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.gui.RealmGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"realm", "realms"}, onlyFor = Command.OnlyFor.PLAYERS)
public class RealmCommand extends UserCommand {
	public RealmCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new RealmGUI(user);
	}
}
