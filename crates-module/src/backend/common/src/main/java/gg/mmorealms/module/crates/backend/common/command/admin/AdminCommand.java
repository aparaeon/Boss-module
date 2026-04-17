package gg.mmorealms.module.crates.backend.common.command.admin;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.crates.backend.common.command.CratesCommand;

@Command(aliases = {"admin"}, parent = CratesCommand.class)
public class AdminCommand extends BackendCommand {

	public AdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

}
