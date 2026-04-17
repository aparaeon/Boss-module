package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.common.dto.event.LoadRealmEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO Enable
//@Command(aliases = {"load"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class LoadCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public LoadCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = IRealm.getByOwner(user);

		if (realm == null) {
			user.sendMessage(config.lang.playerHasNoRealm);
			return;
		}

		// TODO
		switch (realm.getState()) {
			case LOADING -> user.sendMessage(RealmsBackendModule.instance().getConfig().lang.realmStillLoading);
			case CRASHED -> user.sendMessage(
					RealmsBackendModule.instance().getConfig().lang.realmOnCrashingServer
							.parse("pre", "Your")
			);
			case UNLOADING -> user.sendMessage(
					RealmsBackendModule.instance().getConfig().lang.realmUnloading
							.parse("pre", "Your"));
			case null -> new LoadRealmEvent(user.getUUID()).send();
			case LOADED -> user.sendMessage(RealmsBackendModule.instance().getConfig().lang.playerHasOwnRealmLoaded);
		}
	}
}
