package gg.mmorealms.module.essentials.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"spawn"}, onlyFor = Command.OnlyFor.PLAYERS)
public class SpawnCommand extends UserCommand {

	public SpawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		user.send(IServerLocation.of(ServerType.SPAWN));
	}

}
