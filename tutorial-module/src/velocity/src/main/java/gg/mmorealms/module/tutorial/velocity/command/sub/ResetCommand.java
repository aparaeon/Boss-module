package gg.mmorealms.module.tutorial.velocity.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.tutorial.velocity.command.TutorialCommand;
import gg.mmorealms.module.tutorial.velocity.dto.TutorialProgress;

import java.util.List;

@Command(aliases = "reset", parent = TutorialCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class ResetCommand extends VelocityCommand {

	public ResetCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		TutorialProgress tutorialProgress = TutorialProgress.get(player);
		tutorialProgress.reset();
	}
}
