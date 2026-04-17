package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"unload"}, arguments = {"player"}, parent = AdminCommand.class)
public class UnloadCommand extends BackendCommand {
	private @Inject RealmsConfig config;

	public UnloadCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String targetUsernameOrUUID = arguments.getFirst();
		IUser ownerUser = IUser.getByUsernameOrUUID(targetUsernameOrUUID);

		if (ownerUser == null) {
			sendMessage(sender, config.lang.userNotFound);
			return;
		}

		IRealm realm = IRealm.getByOwner(ownerUser);
		if (realm == null) {
			sendMessage(sender, config.lang.playerHasNoRealm);
			return;
		}

		if (realm.getRootLocation() == null) {
			sendMessage(sender, config.lang.realmAlreadyUnloaded);
			return;
		}

		realm.unload();
		sendMessage(sender, config.lang.realmUnloadStart);
	}

}
