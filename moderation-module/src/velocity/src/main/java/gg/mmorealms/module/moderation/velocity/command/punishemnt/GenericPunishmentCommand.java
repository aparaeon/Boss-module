package gg.mmorealms.module.moderation.velocity.command.punishemnt;

import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.generic.Time;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.moderation.velocity.ModerationVelocityModule;
import gg.mmorealms.module.moderation.velocity.config.ModerationConfig;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class
GenericPunishmentCommand extends VelocityCommand {

	private @Inject ModerationConfig config;

	public GenericPunishmentCommand(CommonCommandManager commandManager) {
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
		return List.of("player", "time", "reason");
	}

	private String extractReason(int start, List<String> arguments) {
		return arguments.subList(start, arguments.size()).stream().reduce((a, b) -> a + " " + b).orElse("");
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetUsernameOrUUID = arguments.get(0);
		Time time;
		String reason;

		time = Time.parse(arguments.get(1));

		if (time == null) {
			time = Time.years(10); // This should be permanent ban // TODO Config
			reason = extractReason(1, arguments);
		} else {
			reason = extractReason(2, arguments);
		}

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
			sendMessage(player, config.lang.userNotFound);
			return;
		}


		addPunishment(userPunishments, player, time, reason);

		sendMessage(player, config.lang.punishmentApplied);
	}

	protected abstract void addPunishment(UserPunishments userPunishments, Player staff, Time time, String reason);
}
