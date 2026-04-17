package gg.mmorealms.module.core.backend.common.dto.user;


import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.Lambda;
import com.raduvoinea.utils.message_builder.GenericMessageBuilder;
import com.raduvoinea.utils.redis_manager.event.RedisRequest;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.CommonPermissions;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.common.dto.event.user.UserTeleportEvent;
import gg.mmorealms.module.core.common.dto.event.user.UserTransferEvent;
import gg.mmorealms.module.core.common.dto.server_location.ExactServerLocation;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IUser extends ISavable {

	static IUser getByUUID(UUID uuid) {
		return CoreBackendModule.instance().getUserLoader().getByIdentifier(uuid);
	}

	static @NotNull User getByPlayer(ServerPlayer player) {
		IUser user = CoreBackendModule.instance().getUserLoader().getByIdentifier(player.getUUID());

		if (user instanceof User realUser) {
			return realUser;
		}

		throw new RuntimeException("ServerPlayer resolved to a non User object");
	}

	static @Nullable IUser getByUsername(@NotNull String username) {
		return CoreBackendModule.instance().getUserLoader().getByUsername(username);
	}

	static @Nullable IUser getByUsernameOrUUID(String usernameOrUUID) {
		return CoreBackendModule.instance().getByUUIDOrUsername(
			usernameOrUUID,
			IUser::getByPlayer,
			IUser::getByUUID,
			IUser::getByUsername
		);
	}


	void setUsername(String username);

	String getUsername();

	UUID getUUID();

	void sendMessage(String message);

	void sendActionMessage(String message);

	Boolean hasPermission(@NotNull String permission);

	Integer getCountPermission(@NotNull String basePermission, Range range);

	default ExactServerLocation getServerLocation() {
		return new ExactServerLocation(CoreBackendModule.instance().getEngineManager().getPlayersList().getServer(this.getUUID()));
	}

	Location getBlockLocation();

	Location getLocation();

	default void sendMessage(GenericMessageBuilder<?> message) {
		this.sendMessage(message.toString());
	}

	default void sendActionMessage(GenericMessageBuilder<?> message) {
		this.sendActionMessage(message.toString());
	}

	default Boolean isOnlineOnNetwork() {
		return CoreBackendModule.instance().getEngineManager().getPlayersList().isOnline(this.getUUID());
	}

	default void send(IServerLocation serverLocation, boolean saveLocation) {
		new UserTransferEvent(this.getUUID(), serverLocation, saveLocation).send();
	}

	default void send(IServerLocation serverLocation) {
		new UserTransferEvent(this.getUUID(), serverLocation, !CoreBackendModule.instance().getServerType().equals(ServerType.REALMS)).send();
	}

	@SuppressWarnings("rawtypes")
	default void send(IServerLocation serverLocation, boolean sendSameServerMessage, RedisRequest... events) {
		(new UserTransferEvent(this.getUUID(), serverLocation, !CoreBackendModule.instance().getServerType().equals(ServerType.REALMS), sendSameServerMessage, events)).send();
	}

	@SuppressWarnings("rawtypes")
	default void send(IServerLocation serverLocation, RedisRequest... events) {
		new UserTransferEvent(this.getUUID(), serverLocation, !CoreBackendModule.instance().getServerType().equals(ServerType.REALMS), events).send();
	}

	default void send(IServerLocation serverLocation, Location location) {
		new UserTransferEvent(this.getUUID(), serverLocation, !CoreBackendModule.instance().getServerType().equals(ServerType.REALMS),
			new UserTeleportEvent("", this.getUUID(), location)
		).send();
	}

	default void teleport(Location location) {
		new UserTeleportEvent(this.getUUID(), location).send();
	}

	void kick(String message);

	default Boolean hasCooldown(String type) {
		return IBackendCooldowns.getByUser(this).isActive(type);
	}

	default void setCooldown(String type, Time time) {
		IBackendCooldowns.getByUser(this).set(type, time);
	}

	default Time getCooldown(String type) {
		return Time.milliseconds(IBackendCooldowns.getByUser(this).getRemaining(type));
	}

	default Boolean isAdmin() {
		return hasPermission(CommonPermissions.ADMIN);
	}

	default Boolean isMod() {
		return hasPermission(CommonPermissions.MOD);
	}

	static void executeForUser(String username, ArgLambda<IUser> executor, Lambda failExecutor) {
		executeForUser(username, executor, failExecutor, IUser.class);
	}

	static <T extends IUser> void executeForUser(String username, ArgLambda<T> executor, Lambda failExecutor, Class<T> clazz) {
		if (username.equals("*") || username.equals("__all__")) {
			for (ServerPlayer player : CoreBackendModule.instance().getServer().getPlayerList().getPlayers()) {
				User user = IUser.getByPlayer(player);

				if (!clazz.isInstance(user)) {
					continue;
				}

				executor.run(clazz.cast(user));
			}
			return;
		}

		IUser user = IUser.getByUsername(username);

		if (!clazz.isInstance(user)) {
			failExecutor.run();
			return;
		}

		executor.run(clazz.cast(user));
	}
}
