package gg.mmorealms.module.modpack_rewards.backend.common.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.client.common.dto.ModpackRewardPacket;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.modpack_rewards.backend.common.ModpackRewardsBackendModule;
import gg.mmorealms.module.modpack_rewards.backend.common.config.ModpackRewardsConfig;
import gg.mmorealms.module.modpack_rewards.backend.common.database.ModpackData;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketManager {

	private final ModpackRewardsConfig config;
	private final List<UUID> toCheck = new ArrayList<>();

	public PacketManager() {
		this.config = ModpackRewardsBackendModule.instance().getConfig();

		register();
	}

	public void addToCheck(UUID uuid) {
		toCheck.add(uuid);
	}

	public void check(UUID uuid) {
		if (!toCheck.contains(uuid)) {
			return;
		}

		handleNonModpackPlayer(uuid);
	}

	private void register() {
		ModpackRewardPacket.setHandler((player, packet) -> {
			handleModpackPlayer(player, packet.getModpack());
		});
	}

	private void handleNonModpackPlayer(UUID uuid) {
		toCheck.remove(uuid);

		IUser user = IUser.getByUUID(uuid);
		ModpackData modpackData = ModpackData.getByUUID(uuid);

		if (user == null) {
			return;
		}

		Logger.debug(new MessageBuilder("User {user} did not join with a modpack")
				.parse("user", user.getUsername())
		);

		if (modpackData.hasModpack()) {
			executeCommands(user, config.nonModpackCommands, modpackData.getModpack());
			modpackData.setModpack("");
		}
	}

	protected void handleModpackPlayer(ServerPlayer player, String modpack) {
		ScheduleUtils.runTaskLater(() -> {
			toCheck.remove(player.getUUID());

			if (!config.allowedModpacks.contains(modpack)) {
				Logger.debug(new MessageBuilder("User {user} tried to join with an unsupported modpack: {modpack}")
						.parse("user", player.getGameProfile().getName())
						.parse("modpack", modpack)
				);
				return;
			}

			ModpackData modpackData = ModpackData.getByUUID(player.getUUID());
			IUser user = IUser.getByPlayer(player);

			if (!modpackData.hasModpack()) {
				Logger.debug(new MessageBuilder("User {user} joined with modpack {modpack}")
						.parse("user", user.getUsername())
						.parse("modpack", modpack));
				executeCommands(user, config.modpackCommands, modpack);
				modpackData.setModpack(modpack);
			}
		}, Time.seconds(5));
	}

	private void executeCommands(IUser user, MessageBuilderList commands, String modpack) {
		List<String> parsedCommands = commands
				.parse("user", user.getUsername())
				.parse("modpack", modpack)
				.parse();

		for (String parsedCommand : parsedCommands) {
			ModpackRewardsBackendModule.instance().getServer().getCommands().performPrefixedCommand(
					ModpackRewardsBackendModule.instance().getServer().createCommandSourceStack(),
					parsedCommand
			);
		}
	}

}
