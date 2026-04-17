package gg.mmorealms.module.realms.backend.common.command.sub.admin.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.sub.admin.AdminCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"delete"}, arguments = {"owner"}, onlyFor = Command.OnlyFor.PLAYERS, parent = AdminCommand.class)
public class DeleteCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IUser owner = IUser.getByUsername(arguments.getFirst());

		if (owner == null) {
			user.sendMessage(config.lang.userNotFound);
			return;
		}

		IRealm realm = IRealm.getByOwner(owner);

		if (realm == null) {
			user.sendMessage(config.lang.playerHasNoRealm);
			return;
		}

		realm.delete();
		Logger.info(new MessageBuilder("{uuid} deleted realm {owner_uuid}")
				.parse("uuid", user.getUUID())
				.parse("owner_uuid", realm.getOwnerUUID())
		);

		user.sendMessage(config.lang.realmDeleted);
	}
}
