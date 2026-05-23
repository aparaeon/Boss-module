package gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.AdvancedCommand;
import gg.mmorealms.module.essentials.velocity.manager.WhitelistManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Command(aliases = {"type"}, arguments = {"server_type", "status"},  parent = AdvancedCommand.class)
@Getter
@Setter
public class ServerTypeCommand extends VelocityCommand {

	private @Inject WhitelistManager whitelistManager;

	public ServerTypeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return switch (arguments.size()) {
			case 1 -> Arrays.stream(ServerType.values()).map(ServerType::name).toList();
			case 2 -> List.of("on", "off");
			default -> List.of();
		};
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		this.whitelistManager.setWhitelist(ServerType.valueOf(arguments.get(0)), arguments.get(1).equalsIgnoreCase("on"));
		sendMessage(sender, "Set whitelist for server type " + arguments.get(0) + " to " + arguments.get(1));
	}


}
