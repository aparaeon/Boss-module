package gg.mmorealms.module.warps.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.warps.backend.common.WarpsBackendModule;
import gg.mmorealms.module.warps.backend.common.config.WarpsConfig;
import gg.mmorealms.module.warps.backend.common.dto.Warp;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"add"}, arguments = {"location"}, parent = WarpsCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class AddCommand extends UserCommand {
	private @Inject WarpsConfig config;

	public AddCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String name = arguments.getFirst();

		for (Warp warp : config.warps) {
			if (!warp.getName().equals(name)) {
				continue;
			}
			warp.location = user.getLocation();
			warp.serverType = WarpsBackendModule.instance().getServerType();
			user.sendMessage(config.lang.successOverrideAdd);
			return;
		}

		config.warps.add(new Warp(name, WarpsBackendModule.instance().getServerType(), user.getLocation()));
		user.sendMessage(config.lang.successAdd);
	}

}
