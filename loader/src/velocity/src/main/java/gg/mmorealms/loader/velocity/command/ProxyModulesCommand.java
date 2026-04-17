package gg.mmorealms.loader.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.loader.velocity.VelocityLoader;

import java.util.List;

@Command(aliases = {"proxy_modules"})
public class ProxyModulesCommand extends VelocityCommand {

	public ProxyModulesCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		sendMessage(sender, VelocityLoader.instance().getModuleManager().toString());
	}
}
