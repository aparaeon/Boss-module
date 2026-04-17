package gg.mmorealms.module.kits.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.kits.backend.common.gui.KitGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"kits"}, onlyFor = Command.OnlyFor.PLAYERS)
public class KitsCommand extends UserCommand {
	public KitsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new KitGUI(user).open();
	}
}
