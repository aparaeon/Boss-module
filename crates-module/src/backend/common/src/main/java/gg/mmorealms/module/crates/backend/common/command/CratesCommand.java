package gg.mmorealms.module.crates.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.crates.backend.common.gui.CratesGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"crate", "crates"}, onlyFor = Command.OnlyFor.PLAYERS)
public class CratesCommand extends UserCommand {
	public CratesCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new CratesGUI(user).open();
	}


}
