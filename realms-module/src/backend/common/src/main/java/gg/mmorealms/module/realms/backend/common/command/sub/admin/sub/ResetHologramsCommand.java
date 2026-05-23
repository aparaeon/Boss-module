package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"reset-holograms"}, parent = AdminCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class ResetHologramsCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public ResetHologramsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		Realm realm = IRealm.getAtLocation(user.getLocation());
		if (realm == null) {
			user.sendMessage("Realm is null");
			return;
		}

		realm.resetHolograms();
		user.sendMessage("Triggered hologram reset.");
	}
}
