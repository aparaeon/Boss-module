package gg.mmorealms.module.moderation.velocity.command.punishemnt.impl;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import gg.mmorealms.module.moderation.velocity.config.ModerationConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(aliases = "kick", onlyFor = Command.OnlyFor.PLAYERS, arguments = {"target", "reason..."})
public class KickCommand extends VelocityCommand {

	private @Inject ModerationConfig config;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	public KickCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> defaultAutoComplete() {
		return recommendPlayersList();
	}

	@Override
	protected void executePlayer(Player staff, List<String> arguments) {
		String targetUsernameOrUUID = arguments.get(0);
		String reason = arguments.get(1);

		Player target;

		try {
			UUID uuid = UUID.fromString(targetUsernameOrUUID);
			target = ModerationVelocityModule.instance().getProxy().getPlayer(uuid).orElse(null);
		} catch (Exception e) {
			target = ModerationVelocityModule.instance().getProxy().getPlayer(targetUsernameOrUUID).orElse(null);
		}

		if (target == null) {
			sendMessage(staff, config.lang.userNotFound);
			return;
		}

		ModerationVelocityModule.instance().sendMessage(
				config.lang.kickBroadcast
						.parse("target", target.getUsername())
						.parse("staff", staff.getUsername())
						.parse("reason", reason),
				ModerationVelocityModule.SEE_PUNISHMENT_BROADCAST_PERMISSION
		);

		target.disconnect(miniMessageManager.parse(
				config.lang.kickReasonFormat
						.parse("reason", reason)
		));
	}
}
