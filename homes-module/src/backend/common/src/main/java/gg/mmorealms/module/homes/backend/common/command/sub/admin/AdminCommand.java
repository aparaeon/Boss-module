package gg.mmorealms.module.homes.backend.common.command.sub.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.homes.backend.common.command.HomesCommand;

@Command(aliases = "admin", parent = HomesCommand.class)
public class AdminCommand extends BackendCommand {
	public AdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

}
