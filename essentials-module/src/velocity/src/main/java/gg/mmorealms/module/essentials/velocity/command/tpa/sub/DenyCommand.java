package gg.mmorealms.module.essentials.velocity.command.tpa.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.command.tpa.TPACommand;

import java.util.List;

@Command(aliases = {"deny"}, parent = TPACommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class DenyCommand extends VelocityCommand {

	public DenyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		EssentialsVelocityModule.instance().getTeleportManager().deny(player.getUniqueId());
	}
}
