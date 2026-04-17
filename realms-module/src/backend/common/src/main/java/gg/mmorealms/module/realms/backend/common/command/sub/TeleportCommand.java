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
import gg.mmorealms.module.realms.common.dto.event.IsRealmCrashedRequest;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"teleport", "tp"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class TeleportCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public TeleportCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = IRealm.getByOwner(user);

		if (realm == null) {
			user.sendMessage(config.lang.playerHasNoOwnRealm);
			return;
		}

		switch (realm.getState()) {
			case LOADING -> user.sendMessage(RealmsBackendModule.instance().getConfig().lang.realmStillLoading);
			case CRASHED -> user.sendMessage(
					RealmsBackendModule.instance().getConfig().lang.realmOnCrashingServer
							.parse("pre", "Your")
			);
			case UNLOADING -> user.sendMessage(
					RealmsBackendModule.instance().getConfig().lang.realmUnloading
							.parse("pre", "Your"));
			case null -> user.sendMessage("<red>Something went wrong while trying to load your realm, please relog.");
			case LOADED -> realm.send(user);
		}
	}
}
