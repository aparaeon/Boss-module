package gg.mmorealms.module.kits.backend.common.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;

@Command(aliases = {"delete_command"}, arguments = {"name", "index"}, parent = AdminCommand.class)
public class DeleteCommandCommand extends DeleteOptionCommand {
	public DeleteCommandCommand(CommonCommandManager commandManager) {
		super(commandManager);
		super.setOptionType("command");
	}
}
