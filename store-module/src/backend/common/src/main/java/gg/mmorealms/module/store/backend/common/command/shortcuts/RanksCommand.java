package gg.mmorealms.module.store.backend.common.command.shortcuts;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.store.backend.common.gui.KeysGUI;
import gg.mmorealms.module.store.backend.common.gui.RanksGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"rank", "ranks", "upgrade", "upgrades"}, onlyFor = Command.OnlyFor.PLAYERS)
public class RanksCommand extends UserCommand {
	public RanksCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new RanksGUI(user, user).open();
	}
}