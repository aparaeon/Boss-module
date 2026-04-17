package gg.mmorealms.module.moderation.velocity.command.punishemnt.impl;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import gg.mmorealms.module.moderation.velocity.command.punishemnt.GenericPunishmentCommand;
import gg.mmorealms.module.moderation.velocity.config.ModerationConfig;
import gg.mmorealms.module.moderation.velocity.database.UserBan;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;

@Command(aliases = "ban", onlyFor = Command.OnlyFor.PLAYERS)
public class BanCommand extends GenericPunishmentCommand {

	private @Inject ModerationConfig config;

	public BanCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void addPunishment(UserPunishments userPunishments, Player staff, Time time, String reason) {
		String username;

		Player target = ModerationVelocityModule.instance().getProxy().getPlayer(userPunishments.getUuid()).orElse(null);
		if (target == null) {
			username = userPunishments.getUuid().toString() + " (OFFLINE)";
		} else {
			username = target.getUsername();
		}

		ModerationVelocityModule.instance().sendMessage(
				config.lang.banBroadcast
						.parse("target", username)
						.parse("staff", staff.getUsername())
						.parse("reason", reason)
						.parse("duration", time.toString()),
				ModerationVelocityModule.SEE_PUNISHMENT_BROADCAST_PERMISSION
		);

		userPunishments.addBan(new UserBan(
				userPunishments.getUuid(),
				staff.getUniqueId(),
				reason,
				time
		));

	}
}
