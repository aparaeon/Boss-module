package gg.mmorealms.loader.velocity.dto;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.loader.velocity.VelocityLoader;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface VelocityModule extends CommonModule {

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

	/**
	 * @param uuidOrUsername    username or uuid-string
	 * @param playerBehaviour   Will be executed when the target (provided by either its UUID or username) is online
	 * @param uuidBehaviour     Will be executed whe the target (provided by its UUID) is offline
	 * @param usernameBehaviour Will be executed when the target (provided by its username) is offline
	 * @param <T>               type of object
	 *
	 * @return result
	 */
	default <T> T getByUUIDOrUsername(
		String uuidOrUsername,
		ReturnArgLambda<T, Player> playerBehaviour,
		ReturnArgLambda<T, UUID> uuidBehaviour,
		ReturnArgLambda<T, String> usernameBehaviour
	) {
		try {
			UUID uuid = UUID.fromString(uuidOrUsername);
			Player player = VelocityLoader.instance().getProxy().getPlayer(uuid).orElse(null);

			if (player != null) {
				return playerBehaviour.run(player);
			} else {
				return uuidBehaviour.run(uuid);
			}
		} catch (Exception e) {
			Player player = VelocityLoader.instance().getProxy().getPlayer(uuidOrUsername).orElse(null);

			if (player != null) {
				return playerBehaviour.run(player);
			}

			return usernameBehaviour.run(uuidOrUsername);
		}
	}

	default String getDisplayName(String uuidOrUsername) {
		return getByUUIDOrUsername(
			uuidOrUsername,
			Player::getUsername,
			(uuid) -> uuid + " (OFFLINE)",
			(username) -> username
		);
	}
}
