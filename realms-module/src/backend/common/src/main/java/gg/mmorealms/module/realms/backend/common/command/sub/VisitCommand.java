package gg.mmorealms.module.realms.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.gui.RealmVisitGUI;
import gg.mmorealms.module.realms.common.dto.RealmState;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"visit"}, arguments = {"?player"}, parent = RealmCommand.class)
public class VisitCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public VisitCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("player")) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String playerName = arguments.getFirst();
		if (playerName == null) {
			new RealmVisitGUI(user);
			return;
		}

		IUser owner = IUser.getByUsername(playerName);

		if (owner == null) {
			user.sendMessage(config.lang.userNotFound);
			return;
		}

		if (user.getUUID().equals(owner.getUUID())) {
			user.sendMessage(config.lang.ownRealmVisit);
			return;
		}

		IRealm realm = IRealm.getByOwner(owner);

		if (realm == null) {
			user.sendMessage(config.lang.playerHasNoRealm);
			return;
		}

		RealmState realmState = realm.getState();
		MessageBuilder message = switch (realmState) {
			case LOADING -> RealmsBackendModule.instance().getConfig().lang.realmStillLoading;
			case LOADED -> null;
			case CRASHED -> RealmsBackendModule.instance().getConfig().lang.realmOnCrashingServer;
			case UNLOADING -> RealmsBackendModule.instance().getConfig().lang.realmUnloading;
			case null -> null;
		};

		if (message != null) {
			user.sendMessage(message.parse("pre", "The"));
			return;
		}

		realm.visit(user.getUUID());
	}
}
