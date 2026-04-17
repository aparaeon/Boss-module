package gg.mmorealms.module.realms.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"add", "invite", "inv"}, arguments = {"target"}, onlyFor = Command.OnlyFor.PLAYERS, parent = RealmCommand.class)
public class AddCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public AddCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String targetUsername = arguments.getFirst();
		IUser target = IUser.getByUsername(targetUsername);

		if (target == null) {
			user.sendMessage(config.lang.userNotFound);
			return;
		}

		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_ADD);

		if (realm == null) {
			return;
		}

		if (realm.isMember(target.getUUID())) {
			user.sendMessage(config.lang.alreadyInRealm);
			return;
		}

		realm.addMember(target.getUUID());

		user.sendMessage(config.lang.memberAddedToRealm.parse("target", target.getUsername()));
		target.sendMessage(config.lang.addedToRealm.parse("owner", realm.getOwner().getUsername()));
	}
}
