package gg.mmorealms.loader.backend.common.dto;

import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.loader.common.dto.ServerType;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.UUID;

public interface BackendModule extends CommonModule {

	@Override
	default void registerListener(Class<?> clazz) {
		OnlyOn onlyOn = clazz.getAnnotation(OnlyOn.class);

		if (onlyOn != null && !Arrays.stream(onlyOn.servers()).toList().contains(BackendLoader.instance().getServerType())) {
			Logger.debug("Skipping listener: " + clazz.getName() + " because it is only for server(s): " + Arrays.toString(onlyOn.servers()));
			return;
		}

		CommonModule.super.registerListener(clazz);
	}

	default ServerType getServerType() {
		return BackendLoader.instance().getServerType();
	}

	default void setup() {
		CommonLoader.instance().preRegisterModule(this);
	}

	default void executeCommand(String command) {
		BackendLoader.instance().getServer().getCommands().performPrefixedCommand(
				BackendLoader.instance().getServer().createCommandSourceStack(),
				command
		);
	}

	@Override
	default void sendMessage(Object target, String message) {
		Component parsedMessage = BackendLoader.instance().getMiniMessageManager().parse(message);

		switch (target) {
			case ServerPlayer player -> player.sendSystemMessage(parsedMessage);
			case MinecraftServer server -> server.sendSystemMessage(parsedMessage);
			case CommandSource source -> source.sendSystemMessage(parsedMessage);
			default -> {
				Logger.warn("Attempted to send a message to an unsupported target: " + target.getClass().getName());
				Logger.log(message);
			}
		}
	}

	/**
	 *
	 * @param uuidOrUsername    username or uuid-string
	 * @param playerBehaviour   Will be executed when the target (provided by either its UUID or username) is online
	 * @param uuidBehaviour     Will be executed whe the target (provided by its UUID) is offline
	 * @param usernameBehaviour Will be executed when the target (provided by its username) is offline
	 * @param <T>               type of object
	 * @return result
	 */
	default <T> T getByUUIDOrUsername(
			String uuidOrUsername,
			ReturnArgLambda<T, ServerPlayer> playerBehaviour,
			ReturnArgLambda<T, UUID> uuidBehaviour,
			ReturnArgLambda<T, String> usernameBehaviour
	) {
		try {
			UUID uuid = UUID.fromString(uuidOrUsername);
			ServerPlayer player = BackendLoader.instance().getServer().getPlayerList().getPlayer(uuid);

			if (player != null) {
				return playerBehaviour.run(player);
			} else {
				return uuidBehaviour.run(uuid);
			}
		} catch (Exception e) {
			ServerPlayer player = BackendLoader.instance().getServer().getPlayerList().getPlayerByName(uuidOrUsername);

			if (player != null) {
				return playerBehaviour.run(player);
			}

			return usernameBehaviour.run(uuidOrUsername);
		}
	}

	default void onlyOn(ServerType serverType, Lambda executor) {
		if (!this.getServerType().equals(serverType)) {
			return;
		}

		executor.run();
	}

	default void excluding(ServerType serverType, Lambda executor) {
		if (this.getServerType().equals(serverType)) {
			return;
		}

		executor.run();
	}
}
