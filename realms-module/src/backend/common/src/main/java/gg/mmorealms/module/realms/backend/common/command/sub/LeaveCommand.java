package gg.mmorealms.module.realms.backend.common.command.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.realms.backend.common.command.RealmCommand;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.manager.RealmsUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"leave"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class LeaveCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public LeaveCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.EVERY_MEMBER);

		if (realm == null) {
			return;
		}

		if (!realm.isMember(user.getUUID())) {
			user.sendMessage(config.lang.notRealmMember.parse("owner", realm.getOwner().getUsername()));
			return;
		}

		if (user.getUUID().equals(realm.getOwnerUUID())) {
			user.sendMessage(config.lang.realmOwnerLeaveError);
			return;
		}

		realm.removeMember(user.getUUID());
	}
}
