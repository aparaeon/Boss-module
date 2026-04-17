package gg.mmorealms.module.essentials.velocity.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.ExpirableList;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import gg.mmorealms.loader.common.dto.location.UUIDLocation;
import gg.mmorealms.module.core.common.dto.event.user.UserTeleportEvent;
import gg.mmorealms.module.core.common.dto.event.user.UserTransferEvent;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.dto.EssentialsPermissions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TeleportManager {

	private final ExpirableList<TeleportRequest> teleportRequests = new ExpirableList<>(Time.minutes(1)); // TODO Config
	private final ExpirableList<UUID> cooldowns = new ExpirableList<>(Time.seconds(5)); // TODO Config

	public void create(Player sender, Player receiver, boolean reversed) {
		UUID senderUUID = sender.getUniqueId();
		UUID receiverUUID = receiver.getUniqueId();

		TeleportRequest request = new TeleportRequest(senderUUID, receiverUUID, reversed);

		teleportRequests.add(request);

		sender.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
				.parse(new MessageBuilder("Sent teleport request to {target}") // TODO Config
						.parse("target", receiver.getUsername())
						.parse()
				)
		);

		receiver.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
				.toComponent(new MessageBuilder("Received teleport request from {sender}") // TODO Config
						.parse("sender", sender.getUsername())
						.parse())
				.append(
						EssentialsVelocityModule.instance().getMiniMessageManager().toComponent("<green> [Click to accept]") // TODO Config
								.clickEvent(ClickEvent.clickEvent(
										ClickEvent.Action.RUN_COMMAND,
										"/tpa accept"
								))
				)
		);

	}

	private @Nullable TeleportRequest get(UUID receiver) {
		for (TeleportRequest teleportRequest : teleportRequests.getData()) {
			if (teleportRequest.receiver.equals(receiver)) {
				return teleportRequest;
			}
		}

		return null;
	}

	public void remove(UUID sender, UUID receiver) {
		teleportRequests.removeIf(request -> request.receiver.equals(receiver) && request.sender.equals(sender));
	}

	public void accept(UUID receiverUUID) {
		Player receiver = EssentialsVelocityModule.instance().getProxy().getPlayer(receiverUUID).orElse(null);

		if (receiver == null) {
			Logger.warn("Tried to process teleport request for an offline player"); // TODO Config
			return;
		}

		TeleportRequest request = get(receiverUUID);

		if (request == null) {
			receiver.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
					.parse("You do any pending teleport requests") // TODO Config
			);
			return;
		}

		remove(request.sender, request.receiver);

		Player sender = EssentialsVelocityModule.instance().getProxy().getPlayer(request.sender).orElse(null);

		if (sender == null) {
			receiver.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager().parse("The target player is offline")); // TODO Config
			return;
		}

		sender.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
				.parse("Teleport request accepted") // TODO Config
		);
		receiver.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
				.parse("Teleport request accepted") // TODO Config
		);

		boolean bypassCooldown = sender.hasPermission(EssentialsPermissions.BYPASS_TPA_COOLDOWN);

		if (!bypassCooldown) {
			String teleportBlockedByCooldown1 = "You can only teleport once every 5 seconds"; // TODO Config
			String teleportBlockedByCooldown2 = "The player that requested the teleport has a teleport cooldown."; // TODO Config

			if (cooldowns.contains(sender.getUniqueId())) {
				sender.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
						.parse(teleportBlockedByCooldown1)
				);
				receiver.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
						.parse(teleportBlockedByCooldown2)
				);
				return;
			}

			cooldowns.add(sender.getUniqueId());
		}


		if (request.reversed) {
			teleport(receiver, sender);
		} else {
			teleport(sender, receiver);
		}
	}

	public void teleport(@NotNull Player fromPlayer, @NotNull Player toPlayer) {
		Optional<ServerConnection> originServerOptional = fromPlayer.getCurrentServer();

		if (originServerOptional.isEmpty()) {
			return;
		}

		Optional<ServerConnection> targetServerOpt = toPlayer.getCurrentServer();

		if (targetServerOpt.isEmpty()) {
			fromPlayer.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
					.parse("Target player is offline") // TODO Config
			);
			return;
		}

		String targetServerID = targetServerOpt.get().getServerInfo().getName();

		new UserTransferEvent(fromPlayer.getUniqueId(), targetServerID,
				new UserTeleportEvent(fromPlayer.getUniqueId(), new UUIDLocation(toPlayer.getUniqueId()))
		).send();
	}

	public void deny(UUID receiverUUID) {
		Player receiver = EssentialsVelocityModule.instance().getProxy().getPlayer(receiverUUID).orElse(null);

		if (receiver == null) {
			Logger.warn("Tried to process teleport request for an offline player"); // TODO Config
			return;
		}

		TeleportRequest request = get(receiverUUID);

		if (request == null) {
			receiver.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
					.parse("You have no outstanding teleport requests")// TODO Config
			);
			return;
		}

		remove(request.sender, request.receiver);
		Player sender = EssentialsVelocityModule.instance().getProxy().getPlayer(request.sender).orElse(null);

		if (sender == null) {
			return;
		}

		sender.sendMessage(EssentialsVelocityModule.instance().getMiniMessageManager()
				.parse("Your teleport request has been denied") // TODO Config
		);
	}

	@Getter
	@AllArgsConstructor
	public static class TeleportRequest {
		private final UUID sender;
		private final UUID receiver;

		private final boolean reversed;
	}

}
