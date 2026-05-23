package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.lambda.lambda.non_throwing.ReturnArgLambda;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IPlayerBasedModule<Player> {

	void executeCommand(String command);

	void sendMessage(Object target, String message);

	@Nullable Player getPlayerByUUID(UUID uuid);

	@Nullable Player getPlayerByUsername(String username);

	/**
	 *
	 * @param uuidOrUsername    username or uuid-string
	 * @param playerBehaviour   Will be executed when the target (provided by either its UUID or username) is online
	 * @param uuidBehaviour     Will be executed whe the target (provided by its UUID) is offline
	 * @param usernameBehaviour Will be executed when the target (provided by its username) is offline
	 * @param <Result>          type of object
	 *
	 * @return result
	 */
	default @Nullable <Result> Result getByUUIDOrUsername(
		String uuidOrUsername,
		ReturnArgLambda<Result, Player> playerBehaviour,
		ReturnArgLambda<Result, UUID> uuidBehaviour,
		ReturnArgLambda<Result, String> usernameBehaviour
	) {
		try {
			// uuid
			UUID uuid = UUID.fromString(uuidOrUsername);
			Player player = getPlayerByUUID(uuid);

			if (player != null) {
				// uuid - online
				return playerBehaviour.run(player);
			} else {
				// uuid - offline
				return uuidBehaviour.run(uuid);
			}
		} catch (Exception e) {
			// username
			Player player = getPlayerByUsername(uuidOrUsername);

			if (player != null) {
				// username - online
				return playerBehaviour.run(player);
			} else {
				// username - offline
				return usernameBehaviour.run(uuidOrUsername);
			}
		}
	}

	default @Nullable Player getPlayer(String uuidOrUsername) {
		return this.getByUUIDOrUsername(
			uuidOrUsername,
			(Player player) -> player, // online
			(UUID uuid) -> null, // offline
			(String username) -> null // offline
		);
	}


}
