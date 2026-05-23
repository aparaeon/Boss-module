package gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.core.velocity.dto.EngineServer;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.AdvancedCommand;
import gg.mmorealms.module.essentials.velocity.manager.WhitelistManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"individual"}, arguments = {"server_id", "status"}, parent = AdvancedCommand.class)
@Getter
@Setter
public class IndividualServerCommand extends VelocityCommand {

	private @Inject ServerManager serverManager;
	private @Inject WhitelistManager whitelistManager;

	public IndividualServerCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return switch (arguments.size()) {
			case 1 -> serverManager.getServers().stream().map(EngineServer::getServerID).toList();
			case 2 -> List.of("on", "off");
			default -> List.of();
		};
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		this.whitelistManager.setWhitelist(arguments.get(0), arguments.get(1).equalsIgnoreCase("on"));
		sendMessage(sender, "Whitelist for server " + arguments.get(0) + " set to " + arguments.get(1));
	}


}
