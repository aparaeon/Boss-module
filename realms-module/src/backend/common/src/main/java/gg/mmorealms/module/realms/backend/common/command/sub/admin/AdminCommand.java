package gg.mmorealms.module.realms.backend.common.command.sub.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"admin"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class AdminCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public AdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		user.sendMessage(config.lang.adminHelpMessage);
	}

}
