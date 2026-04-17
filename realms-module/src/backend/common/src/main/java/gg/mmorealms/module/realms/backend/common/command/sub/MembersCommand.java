package gg.mmorealms.module.realms.backend.common.command.sub;

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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Command(aliases = {"members"}, parent = RealmCommand.class, onlyFor = Command.OnlyFor.PLAYERS)
public class MembersCommand extends UserCommand {
	private @Inject RealmsConfig config;

	public MembersCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IRealm realm = RealmsUtils.getCurrentRealm(user, RealmPermission.COMMAND_MEMBERS);

		if (realm == null) {
			return;
		}

		for (Map.Entry<UUID, TrustLevel> member : realm.getMembers().entrySet()) {
			IUser memberUser = IUser.getByUUID(member.getKey());
			user.sendMessage(config.lang.realmMemberEntry
					.parse("username", memberUser.getUsername())
					.parse("permission", member.getValue().getDisplayName())
			);
		}
	}
}
