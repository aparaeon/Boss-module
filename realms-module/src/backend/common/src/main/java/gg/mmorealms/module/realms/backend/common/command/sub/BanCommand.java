package gg.mmorealms.module.realms.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


// TODO: Maybe when a player tries to tpa to another one:
//  - check destination if it is a realm
//  - check if user is banned on it
@Command(aliases = {"ban"}, arguments = {"player"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class BanCommand extends UserCommand {

	private @Inject RealmsConfig config;

	public BanCommand(CommonCommandManager commandManager) {
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

		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_BAN);

		if (realm == null) {
			return;
		}

		realm.addBan(targetUser.getUUID());

		int senderTrustLevel = realm.getTrustLevel(user.getUUID()).getLevel();
		TrustLevel targetTrustLevel = realm.getTrustLevel(targetUser.getUUID());

		if (targetTrustLevel != null && targetTrustLevel.getLevel() >= senderTrustLevel) {
			user.sendMessage(config.lang.invalidBan);
			return;
		}

		user.sendMessage(config.lang.playerBanned.parse("user", targetUser.getUsername()));
		targetUser.sendMessage(config.lang.bannedNotice.parse("owner", realm.getOwner().getUsername()));

		IRealm targetRealm = IRealm.getAtLocation(targetUser.getLocation());
		if (targetRealm == null) {
			return;
		}

		if (!targetRealm.getOwnerUUID().equals(realm.getOwnerUUID())) {
			return;
		}

		targetUser.send(IServerLocation.of(ServerType.SPAWN));
	}
}
