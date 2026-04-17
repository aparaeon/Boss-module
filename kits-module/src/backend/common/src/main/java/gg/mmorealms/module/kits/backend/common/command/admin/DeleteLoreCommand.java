package gg.mmorealms.module.kits.backend.common.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;

@Command(aliases = {"delete_lore"}, arguments = {"name", "index"}, parent = AdminCommand.class)
public class DeleteLoreCommand extends DeleteOptionCommand {
	public DeleteLoreCommand(CommonCommandManager commandManager) {
		super(commandManager);
		super.setOptionType("lore");
	}
}
