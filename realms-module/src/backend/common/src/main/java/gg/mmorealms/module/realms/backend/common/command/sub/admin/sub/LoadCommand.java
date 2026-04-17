package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.LoadRealmEvent;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"load"}, arguments = {"player"}, parent = AdminCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class LoadCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public LoadCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String playerName = arguments.getFirst();
		IUser ownerUser = IUser.getByUsername(playerName);

		if (ownerUser == null) {
			user.sendMessage(config.lang.userNotFound);
			return;
		}

		IRealm realm = IRealm.getByOwner(ownerUser);
		if (realm == null) {
			user.sendMessage(config.lang.playerHasNoRealm);
			return;
		}

		if (realm.getState() == RealmState.LOADED) {
			user.sendMessage(config.lang.realmAlreadyLoaded);
			return;
		}

		new LoadRealmEvent(user.getUUID(), realm.getOwnerUUID()).send();
	}
}
