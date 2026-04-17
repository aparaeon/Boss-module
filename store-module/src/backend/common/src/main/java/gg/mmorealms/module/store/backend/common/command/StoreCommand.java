package gg.mmorealms.module.store.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.store.backend.common.gui.StoreGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"store"}, onlyFor = Command.OnlyFor.PLAYERS)
public class StoreCommand extends UserCommand {
	public StoreCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new StoreGUI(user).open();
	}
}
