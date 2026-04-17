package gg.mmorealms.module.discord_link.velocity.command.sub.admin.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.discord_link.velocity.DiscordLinkVelocityModule;
import gg.mmorealms.module.discord_link.velocity.command.sub.admin.AdminSubCommand;
import gg.mmorealms.module.discord_link.velocity.config.DiscordLinkConfig;
import gg.mmorealms.module.discord_link.velocity.database.DiscordLinkedUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(aliases = {"unlink"}, arguments = {"username/uuid/discordID"}, onlyFor = Command.OnlyFor.PLAYERS, parent = AdminSubCommand.class)
public class UnlinkSubCommand extends VelocityCommand {

	public @Inject DiscordLinkConfig config;

	public UnlinkSubCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return recommendPlayersList();
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetUsernameOrUUIDOrDiscordID = arguments.getFirst();
		DiscordLinkedUser linkedUser = null;

		try {
			UUID uuid = UUID.fromString(targetUsernameOrUUIDOrDiscordID);

			linkedUser = DiscordLinkedUser.getByUUID(uuid);
		} catch (Exception e) {
			Player target = DiscordLinkVelocityModule.instance().getProxy().getPlayer(targetUsernameOrUUIDOrDiscordID).orElse(null);

			if (target != null) {
				linkedUser = DiscordLinkedUser.getByUUID(target.getUniqueId());
			}
		}

		if (linkedUser == null) {
			try {
				long discordID = Long.parseLong(targetUsernameOrUUIDOrDiscordID);
				linkedUser = DiscordLinkedUser.getByDiscordID(discordID);
			} catch (NumberFormatException e) {
				Logger.error(e);
			}
		}

		if (linkedUser == null) {
			sendMessage(player, config.lang.couldNotFindLinkedUser);
			return;
		}

		linkedUser.delete();
		sendMessage(player, config.lang.unlinked
				.parse("target_uuid", linkedUser.getUuid())
				.parse("target_discord_id", linkedUser.getDiscordID())
		);
	}
}
