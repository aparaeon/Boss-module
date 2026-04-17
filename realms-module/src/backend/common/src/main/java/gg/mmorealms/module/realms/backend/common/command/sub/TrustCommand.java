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
import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"trust"}, arguments = {"player", "permission"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class TrustCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public TrustCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("player")) {
			return recommendPlayersList();
		}
		if (argument.equals("permission")) {
			return TrustLevel.getAcceptedLevels();
		}
		return null;
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		String targetUsername = arguments.get(0);
		String trustLevelName = arguments.get(1);
		TrustLevel trustLevel = TrustLevel.fromName(trustLevelName);

		if (trustLevel == TrustLevel.UNKNOWN) {
			user.sendMessage(config.lang.invalidParameter.parse("parameter", "trust level"));
			return;
		}

		IUser target = IUser.getByUsername(targetUsername);

		if (target == null) {
			user.sendMessage(config.lang.userNotFound);
			return;
		}

		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_TRUST);
		if (realm == null) {
			return;
		}

		int senderTrustLevel = realm.getTrustLevel(user.getUUID()).getLevel();
		TrustLevel targetTrustLevel = realm.getTrustLevel(target.getUUID());

		if (targetTrustLevel != null && targetTrustLevel.getLevel() >= senderTrustLevel) {
			user.sendMessage(config.lang.invalidTrust);
			return;
		}

		if (trustLevel.getLevel() >= senderTrustLevel) {
			user.sendMessage(config.lang.invalidTrustLimit);
			return;
		}

		realm.setMemberTrustLevel(target.getUUID(), trustLevel);

		user.sendMessage(config.lang.grantedPermission
				.parse("target", target.getUsername())
				.parse("permission", trustLevel.getDisplayName()));

		target.sendMessage(config.lang.receivedPermission
				.parse("owner", realm.getOwner().getUsername())
				.parse("permission", trustLevel.getDisplayName()));
	}
}
