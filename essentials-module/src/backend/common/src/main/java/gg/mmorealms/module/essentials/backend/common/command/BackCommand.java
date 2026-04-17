package gg.mmorealms.module.essentials.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.NetworkLocation;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.essentials.common.dto.event.UserLastNetworkLocationRequest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"back"}, onlyFor = Command.OnlyFor.PLAYERS)
public class BackCommand extends UserCommand {
	public BackCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		NetworkLocation lastLocation = new UserLastNetworkLocationRequest(user.getUUID()).sendAndGet();

		if (lastLocation == null) {
			user.sendMessage("There is no last location saved!"); //TODO: Lang
			return;
		}

		NetworkLocation currentLocation = new NetworkLocation(
				BackendLoader.instance().getServerID(),
				user.getLocation()
		);

		user.send(IServerLocation.of(lastLocation.getServer()), lastLocation.getLocation());
	}
}
