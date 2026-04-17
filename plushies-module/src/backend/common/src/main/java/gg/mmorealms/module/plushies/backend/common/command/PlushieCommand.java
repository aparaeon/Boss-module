package gg.mmorealms.module.plushies.backend.common.command;

import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;

@Command(aliases = {"plushie", "plushies"})
public class PlushieCommand extends BackendCommand {

	public PlushieCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

}
