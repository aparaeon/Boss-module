package gg.mmorealms.module.kits.backend.common.command.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.kits.backend.common.config.KitsConfig;

@Command(aliases = {"add_lore"}, arguments = {"name", "lore..."}, parent = AdminCommand.class)
public class AddLoreCommand extends AddOptionCommand {

	private @Inject KitsConfig config;

	public AddLoreCommand(CommonCommandManager commandManager) {
		super(commandManager);
		super.setOptionType("lore");
	}

}