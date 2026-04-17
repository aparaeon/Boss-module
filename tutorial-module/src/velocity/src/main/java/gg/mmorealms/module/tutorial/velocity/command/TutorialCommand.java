package gg.mmorealms.module.tutorial.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
@Command(aliases = "tutorial", onlyFor = Command.OnlyFor.PLAYERS)
public class TutorialCommand extends VelocityCommand {

	public TutorialCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

}
