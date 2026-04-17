package gg.mmorealms.module.store.backend.common.command.shortcuts;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.store.backend.common.gui.KeysGUI;
import gg.mmorealms.module.store.backend.common.gui.StoreGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"key", "keys"}, onlyFor = Command.OnlyFor.PLAYERS)
public class KeysCommand extends UserCommand {
	public KeysCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new KeysGUI(user, user).open();
	}
}