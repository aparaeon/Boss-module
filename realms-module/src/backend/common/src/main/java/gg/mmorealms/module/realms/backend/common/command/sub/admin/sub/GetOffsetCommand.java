package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"get_offset"}, onlyFor = Command.OnlyFor.PLAYERS, parent = AdminCommand.class)
public class GetOffsetCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public GetOffsetCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = IRealm.getAtLocation(user.getLocation());

		if (realm == null) {
			user.sendMessage(config.lang.notInARealm);
			return;
		}

		RegionLocation location = realm.getRootLocation();

		Location offset = user.getLocation().offsetNew(
				location.toLocation().multiplyNew(-1)
		);

		user.sendMessage("Offset: " + offset);
	}
}
