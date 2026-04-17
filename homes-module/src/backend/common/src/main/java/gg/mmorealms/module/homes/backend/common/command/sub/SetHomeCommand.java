package gg.mmorealms.module.homes.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;

@Command(aliases = {"sethome", "set_home"}, arguments = {"name"}, onlyFor = Command.OnlyFor.PLAYERS)
public class SetHomeCommand extends AddCommand {
	public SetHomeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}
}
