package gg.mmorealms.module.essentials.velocity.command.tpa;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;

@Command(aliases = {"tpahere", "tpa_here"}, onlyFor = Command.OnlyFor.PLAYERS)
public class TPAHereCommand extends TPACommandBase {

	public TPAHereCommand(CommonCommandManager commandManager) {
		super(commandManager, true);
	}

}
