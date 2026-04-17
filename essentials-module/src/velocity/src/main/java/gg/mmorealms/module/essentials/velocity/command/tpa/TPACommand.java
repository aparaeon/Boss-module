package gg.mmorealms.module.essentials.velocity.command.tpa;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;

@Command(aliases = {"tpa"}, onlyFor = Command.OnlyFor.PLAYERS)
public class TPACommand extends TPACommandBase {

	public TPACommand(CommonCommandManager commandManager) {
		super(commandManager, false);
	}

}
