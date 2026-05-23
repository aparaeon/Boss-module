package gg.mmorealms.loader.velocity.dto;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.loader.common.dto.IPlayerBasedModule;
import gg.mmorealms.loader.common.utils.MojangUtils;
import gg.mmorealms.loader.velocity.VelocityLoader;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface VelocityModule extends CommonModule, IPlayerBasedModule<Player> {

	default void velocity$afterConstruct() {
		try {
			CommonLoader.instance().preRegisterModule(this);
		} catch (Throwable throwable) {
			Logger.error(throwable);
		}
	}

	private String getID() {
		return this.getClass().getAnnotation(Module.class).id();
	}

	default void sendMessage(GenericMessageBuilder<?> message) {
		sendMessage(message.toString());
	}

	default void sendMessage(String message) {
		sendMessage(VelocityLoader.instance().getMiniMessageManager().parse(message));
	}

	default void sendMessage(Component message) {
		VelocityLoader.instance().getProxy().sendMessage(message);
	}

	default void sendMessage(GenericMessageBuilder<?> message, String permission) {
		sendMessage(message.toString(), permission);
	}

	default void sendMessage(String message, String permission) {
		sendMessage(VelocityLoader.instance().getMiniMessageManager().parse(message), permission);
	}

	default void sendMessage(Component message, String permission) {
		for (Player player : VelocityLoader.instance().getProxy().getAllPlayers()) {
			if (!player.hasPermission(permission)) {
				continue;
			}

			player.sendMessage(message);
		}
	}

	@Override
	default void sendMessage(Object target, String message) {
		Component parsedMessage = VelocityLoader.instance().getMiniMessageManager().parse(message);

		switch (target) {
			case Player player -> player.sendMessage(parsedMessage);
			case ProxyServer server -> server.sendMessage(parsedMessage);
			case CommandSource source -> source.sendMessage(parsedMessage);
			case Audience audience -> audience.sendMessage(parsedMessage);
			default -> {
				Logger.warn("Attempted to send a message to an unsupported target: " + target.getClass().getName());
				Logger.log(message);
			}
		}
	}

	@Override
	default @org.jetbrains.annotations.Nullable Player getPlayerByUsername(String username) {
		return VelocityLoader.instance().getProxy().getPlayer(username).orElse(null);
	}

	@Override
	default void executeCommand(String command) {
		VelocityLoader.instance().getProxy().getCommandManager().executeAsync(
			VelocityLoader.instance().getProxy().getConsoleCommandSource(),
			command
		);
	}

	/**
	 * Resolves a username or uuid-string to a {@link UUID}, hitting Mojang's API as a fallback when the
	 * target is offline and only a username was provided.
	 *
	 * @param uuidOrUsername username or uuid-string
	 * @return resolved UUID, or {@code null} if the username could not be resolved
	 */
	@Override
	default @Nullable UUID getUUID(@NotNull String uuidOrUsername) {
		return getByUUIDOrUsername(
			uuidOrUsername,
			Player::getUniqueId,
			uuid -> uuid,
			MojangUtils::getUUID
		);
	}

	default String getDisplayName(String uuidOrUsername) {
		return getByUUIDOrUsername(
			uuidOrUsername,
			Player::getUsername,
			(uuid) -> uuid + " (OFFLINE)",
			(username) -> username
		);
	}

	@Override
	default @Nullable Player getPlayerByUUID(UUID uuid) {
		return VelocityLoader.instance().getProxy().getPlayer(uuid).orElse(null);
	}
}
