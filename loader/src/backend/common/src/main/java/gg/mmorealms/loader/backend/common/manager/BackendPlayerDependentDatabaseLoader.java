package gg.mmorealms.loader.backend.common.manager;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgLambda;
import com.raduvoinea.utils.lambda.lambda.non_throwing.ArgsLambda;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.common.dto.PlayerMethod;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.dto.event.impl.UserServerRequest;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BackendPlayerDependentDatabaseLoader<
	ObjectInterface extends ISavable,
	LoadedObject extends ObjectInterface,
	RemoteObject extends ObjectInterface
	> extends SyncedDatabaseLoader<UUID, ObjectInterface, LoadedObject, RemoteObject> {

	private static final Set<UUID> LOADED_PLAYER_UUIDs = ConcurrentHashMap.newKeySet();
	private static final List<PlayerMethod<ServerPlayer>> PLAYER_METHODS = new ArrayList<>();

	public BackendPlayerDependentDatabaseLoader(
		Class<ObjectInterface> objectInterfaceClass,
		Class<LoadedObject> loadedObjectClass,
		Class<RemoteObject> remoteObjectClass
	) {
		super(objectInterfaceClass, loadedObjectClass, remoteObjectClass);
	}

	public BackendPlayerDependentDatabaseLoader(
		Class<ObjectInterface> objectInterfaceClass,
		Class<LoadedObject> loadedObjectClass,
		Class<RemoteObject> remoteObjectClass,
		Time autoSaveInterval
	) {
		super(objectInterfaceClass, loadedObjectClass, remoteObjectClass, autoSaveInterval);
	}

	@Override
	protected boolean shouldClearCache(@NotNull UUID uuid, @NotNull LoadedObject loadedObject) {
		return BackendLoader.instance().getServer().getPlayerList().getPlayer(uuid) == null;
	}

	@Override
	@SneakyThrows(value = {InterruptedException.class})
	protected @Nullable ObjectInterface locallyOnlineObjectBehaviour(@NotNull UUID uuid) {
		String remoteServer;
		int attempts = 0;

		// If the player is in transit wait for the player to arrive to the target server
		do {
			remoteServer = getRemoteServer(uuid);

			if (remoteServer == null) {
				return offlineObjectBehavior(uuid);
			}

			ServerPlayer player = BackendLoader.instance().getServer().getPlayerList().getPlayer(uuid);

			if (player != null) {
				return super.locallyOnlineObjectBehaviour(uuid);
			}

			//noinspection BusyWait
			Thread.sleep(ATTEMPT_TIMEOUT);
			attempts++;
		} while (BackendLoader.instance().getServerID().equals(remoteServer) && attempts < MAX_ATTEMPTS);

		return remotelyOnlineObjectBehaviour(uuid, remoteServer);
	}

	@Override
	public @Nullable String getRemoteServer(UUID uuid) {
		if (LOADED_PLAYER_UUIDs.contains(uuid) || BackendLoader.instance().getServer().getPlayerList().getPlayer(uuid) != null) {
			return BackendLoader.instance().getServerID();
		}

		return new UserServerRequest(uuid).sendAndGet();
	}

	public static void registerPlayerMethod(@NotNull String name,
	                                        @NotNull ArgLambda<ServerPlayer> loader,
	                                        @NotNull ArgLambda<ServerPlayer> saver,
	                                        @Nullable Time autoSaveInterval
	) {
		registerPlayerMethod(name, loader, (player, evictFromCache) -> saver.run(player), autoSaveInterval);
	}

	public static void registerPlayerMethod(@NotNull String name,
	                                        @NotNull ArgLambda<ServerPlayer> loader,
	                                        @NotNull ArgsLambda<ServerPlayer, Boolean> saver,
	                                        @Nullable Time autoSaveInterval
	) {
		Logger.debug(new MessageBuilder("Registering player method {name} with auto-save interval {interval}")
			.parse("name", name)
			.parse("interval", autoSaveInterval != null ? autoSaveInterval.toString() : "none")
			.parse());

		CancelableTimeTask autoSaveTask = null;
		if (autoSaveInterval != null) {
			autoSaveTask = ScheduleUtils.runTaskTimer(() -> {
				Logger.debug(new MessageBuilder("Auto-saving {data} for all online players...")
					.parse("data", name)
					.parse()
				);
				for (UUID onlinePlayerUUID : LOADED_PLAYER_UUIDs) {
					ServerPlayer player = BackendLoader.instance().getServer().getPlayerList().getPlayer(onlinePlayerUUID);
					if (player == null) {
						Logger.warn(new MessageBuilder("Player {uuid} is not online. Skipping auto-save...")
							.parse("uuid", onlinePlayerUUID)
							.parse());
						continue;
					}
					saver.run(player, false);
				}
			}, autoSaveInterval);
		}

		PLAYER_METHODS.add(new PlayerMethod<>(name, loader, saver, autoSaveTask));
	}

	public static synchronized void joined(@NotNull ServerPlayer player) {
		if (LOADED_PLAYER_UUIDs.contains(player.getUUID())) {
			Logger.error(new MessageBuilder("{username} ({uuid}) joined the server but it was in the LOADED_PLAYER_UUIDs map ({loaded_uuids})")
				.parse("username", player.getName().getString())
				.parse("uuid", player.getStringUUID())
				.parse("loaded_uuids", LOADED_PLAYER_UUIDs));
			return;
		}

		for (DatabaseLoader<?, ?, ?> _loader : ALL) {
			if (!(_loader instanceof BackendPlayerDependentDatabaseLoader<?, ?, ?> loader)) {
				continue;
			}
			loader.onJoin(player);
		}

		for (PlayerMethod<ServerPlayer> playerMethod : PLAYER_METHODS) {
			playerMethod.getLoader().run(player);
		}

		LOADED_PLAYER_UUIDs.add(player.getUUID());
	}

	public static synchronized void left(@NotNull ServerPlayer player) {
		if (!LOADED_PLAYER_UUIDs.contains(player.getUUID())) {
			return;
		}

		for (DatabaseLoader<?, ?, ?> _loader : ALL) {
			if (!(_loader instanceof BackendPlayerDependentDatabaseLoader<?, ?, ?> loader)) {
				continue;
			}

			Logger.debug("Clearing cache for player " + player.getUUID() + " in loader " + loader.getClass().getSimpleName());

			loader.onLeave(player);
			loader.clearCache(player.getUUID(), true);
		}

		for (PlayerMethod<ServerPlayer> playerMethod : PLAYER_METHODS) {
			Logger.debug("Saving " + playerMethod.getName() + " for player " + player.getUUID());
			playerMethod.getSaver().run(player, true);
		}

		LOADED_PLAYER_UUIDs.remove(player.getUUID());
	}

	public abstract void onJoin(@NotNull ServerPlayer player);

	public abstract void onLeave(@NotNull ServerPlayer player);

}
