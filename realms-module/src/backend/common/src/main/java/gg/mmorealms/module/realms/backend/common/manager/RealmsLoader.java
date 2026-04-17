package gg.mmorealms.module.realms.backend.common.manager;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.loader.common.exception.DatabaseObjectCreationException;
import gg.mmorealms.module.core.backend.common.dto.CommonPermissions;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.realm.IRealm;
import gg.mmorealms.module.realms.backend.common.dto.realm.Realm;
import gg.mmorealms.module.realms.backend.common.dto.realm.RemoteRealm;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.GetRealmServerRequest;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RealmsLoader extends SyncedDatabaseLoader<UUID, IRealm, Realm, RemoteRealm> {

	public RealmsLoader() {
		super(IRealm.class, Realm.class, RemoteRealm.class, Time.minutes(10));
	}

	@Override
	protected boolean shouldClearCache(@NotNull UUID uuid, @NotNull Realm realm) {
		for (UUID membersUUID : realm.getMembers().keySet()) {
			if (IUser.getByUUID(membersUUID).isOnlineOnNetwork()) {
				Logger.info("Found member online for realm: " + realm.getOwnerUUID());
				return false;
			}
		}

		for (ServerPlayer serverPlayer : realm.getPlayersOnRealm()) {
			if (LuckPermsUtils.checkPermission(ServerPlayer.class, serverPlayer, CommonPermissions.MOD)) {
				Logger.info("Found mod online for realm: " + realm.getOwnerUUID());
				return false;
			}
		}

		Logger.info("Clear cache for realm: " + realm.getOwnerUUID());
		realm.setState(RealmState.UNLOADING);
		return true;
	}

	@Override
	public @NotNull RemoteRealm createRemoteObject(@NotNull UUID ownerUUID, @NotNull String server) {
		return new RemoteRealm(ownerUUID, server);
	}

	@Override
	protected @Nullable Realm createObject(@NotNull UUID uuid) {
		return null; // Realms should not be automatically created
	}

	@Override
	public @Nullable String getRemoteServer(UUID uuid) {
		return new GetRealmServerRequest(uuid).sendAndGet();
	}

	public @Nullable Realm getByRootLocation(@NotNull Location location) {
		RegionLocation rootLocation = RegionLocation.convert(location);
		// TODO: Change to REALMS_SIZE
		rootLocation.normalize(2);

		return getByRootLocation(rootLocation);
	}

	public @Nullable Realm getByRootLocation(@NotNull RegionLocation rootLocation) {
		for (UUID uuid : cache.keySet()) {
			Realm realm = cache.get(uuid);

			if (realm == null) {
				continue;
			}

			RegionLocation realmRootLocation = realm.getRootLocation();

			if(realmRootLocation == null) {
				continue;
			}

			if (realmRootLocation.equals(rootLocation)) {
				return realm;
			}
		}

		return null;
	}

	@Override
	public @Nullable Realm loadObject(@NotNull UUID uuid) {
		return super.loadObject(uuid);
	}

	@Override
	public @Nullable IRealm getByIdentifier(@NotNull UUID uuid) {
		try {
			return super.getByIdentifier(uuid);
		} catch (DatabaseObjectCreationException exception) {
			return null;
		}
	}

	@Override
	protected void beforeCacheAutoCommit() {
		CompletableFuture<Boolean> future = ServerTickEvent.runOnTick(() -> {
			RealmsBackendModule.instance().getServer().saveEverything(true, true, true);
			return true;
		}, 0);

		try {
			future.orTimeout(10, TimeUnit.MINUTES).get();
		} catch (InterruptedException | ExecutionException exception) {
			Logger.error(exception);
		}
	}
}
