package gg.mmorealms.module.moderation.velocity.command.punishemnt.revoke.impl;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.moderation.velocity.command.punishemnt.revoke.GenericPunishmentRevokeCommand;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;

@Command(aliases = "unban", onlyFor = Command.OnlyFor.PLAYERS)
public class UnbanCommand extends GenericPunishmentRevokeCommand {

	public UnbanCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void revokePunishment(UserPunishments userPunishments) {
		userPunishments.revokeBan();
	}

}
