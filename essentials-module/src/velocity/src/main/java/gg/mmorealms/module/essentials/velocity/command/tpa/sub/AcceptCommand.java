package gg.mmorealms.module.essentials.velocity.command.tpa.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.command.tpa.TPACommand;

import java.util.List;

@Command(aliases = {"accept"}, parent = TPACommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class AcceptCommand extends VelocityCommand {

	public AcceptCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		EssentialsVelocityModule.instance().getTeleportManager().accept(player.getUniqueId());
	}
}
