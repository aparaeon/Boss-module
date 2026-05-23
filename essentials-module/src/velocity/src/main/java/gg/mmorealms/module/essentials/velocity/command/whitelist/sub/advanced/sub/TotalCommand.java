package gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.essentials.velocity.command.whitelist.sub.advanced.AdvancedCommand;
import gg.mmorealms.module.essentials.velocity.manager.WhitelistManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"total"}, arguments = "status", parent = AdvancedCommand.class)
@Getter
@Setter
public class TotalCommand extends VelocityCommand {

	private @Inject WhitelistManager whitelistManager;

	public TotalCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return List.of("on", "off");
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		boolean newState = arguments.getFirst().equalsIgnoreCase("on");

		whitelistManager.setWhitelist(newState);

		sendMessage(sender, "Whitelist total " + (newState ? "enabled" : "disabled") + ".");
	}


}
