package gg.mmorealms.module.moderation.velocity.command.punishemnt.revoke;

import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import gg.mmorealms.module.moderation.velocity.config.ModerationConfig;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class GenericPunishmentRevokeCommand extends VelocityCommand {

	private @Inject ModerationConfig config;

	public GenericPunishmentRevokeCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		if (arguments.size() == 1) {
			return recommendPlayersList();
		}

		return List.of();
	}

	@Override
	public List<String> getArguments() {
		return List.of("player");
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetUsernameOrUUID = arguments.getFirst();

		UserPunishments userPunishments = null;

		try {
			UUID uuid = UUID.fromString(targetUsernameOrUUID);
			Player target = ModerationVelocityModule.instance().getProxy().getPlayer(uuid).orElse(null);

			if (target != null) {
				userPunishments = UserPunishments.getByPlayer(target);
			} else {
				userPunishments = UserPunishments.getByUUID(uuid);
			}
		} catch (Exception e) {
			Player target = ModerationVelocityModule.instance().getProxy().getPlayer(targetUsernameOrUUID).orElse(null);

			if (target != null) {
				userPunishments = UserPunishments.getByPlayer(target);
			}
		}

		if (userPunishments == null) {
			sendMessage(player, "Target not found");
			return;
		}

		revokePunishment(userPunishments);

		sendMessage(player, config.lang.punishmentRevoked);
	}

	protected abstract void revokePunishment(UserPunishments userPunishments);
}
