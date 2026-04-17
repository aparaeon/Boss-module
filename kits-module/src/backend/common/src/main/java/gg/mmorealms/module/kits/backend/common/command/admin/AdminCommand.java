package gg.mmorealms.module.kits.backend.common.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.kits.backend.common.command.KitsCommand;

@Command(aliases = {"admin"}, onlyFor = Command.OnlyFor.PLAYERS, parent = KitsCommand.class)
public class AdminCommand extends BackendCommand {
	public AdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}
}
