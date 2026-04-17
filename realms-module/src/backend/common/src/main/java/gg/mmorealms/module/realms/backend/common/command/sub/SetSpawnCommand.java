package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"set_spawn"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class SetSpawnCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public SetSpawnCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_SET_SPAWN);

		if (realm == null) {
			return;
		}

		realm.setSpawnOffset(user.getLocation().offset(realm.getRootLocation().toLocation().multiply(-1)));
		user.sendMessage(config.lang.setSpawnMessage);
	}
}
