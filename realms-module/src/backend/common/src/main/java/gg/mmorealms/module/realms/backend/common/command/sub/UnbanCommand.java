package gg.mmorealms.module.realms.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"unban"}, arguments = {"player"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class UnbanCommand extends UserCommand {
	private final RealmsConfig config = RealmsBackendModule.instance().getConfig();

	public UnbanCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		return recommendPlayersList();
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String playerName = arguments.getFirst();

		IUser targetUser = IUser.getByUsername(playerName);
		if (targetUser == null) {
			user.sendMessage(config.lang.userNotFound);
			return;
		}

		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_UNBAN);

		if (realm == null) {
			return;
		}

		realm.removeBan(targetUser.getUUID());

		user.sendMessage(config.lang.playerUnbanned.parse("user", targetUser.getUsername()));
		targetUser.sendMessage(config.lang.unbannedNotice.parse("owner", realm.getOwner().getUsername()));
	}
}
