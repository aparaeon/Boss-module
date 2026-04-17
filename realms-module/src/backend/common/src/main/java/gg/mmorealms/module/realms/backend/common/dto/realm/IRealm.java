package gg.mmorealms.module.realms.backend.common.dto.realm;

import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.CommonPermissions;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.common.dto.server_location.IServerLocation;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.dto.ChunkLocation;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.RealmSettings;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import gg.mmorealms.module.realms.common.dto.RealmState;
import gg.mmorealms.module.realms.common.dto.event.GetRealmServerRequest;
import gg.mmorealms.module.realms.common.dto.event.GetRealmStateRequest;
import gg.mmorealms.module.realms.common.dto.event.RealmStateChangeEvent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IRealm extends ISavable {

	int REGIONS_COUNT = 2;

	static @Nullable IRealm getByOwner(@NotNull ServerPlayer owner) {
		return getByOwner(owner.getUUID());
	}

	static @Nullable IRealm getByOwner(@NotNull IUser owner) {
		return getByOwner(owner.getUUID());
	}

	static @Nullable IRealm getByOwner(@NotNull UUID ownerUUID) {
		return RealmsBackendModule.instance().getRealmsLoader().getByIdentifier(ownerUUID);
	}

	static @Nullable Realm getAtLocation(Location location) {
		return RealmsBackendModule.instance().getRealmsLoader().getByRootLocation(location);
	}

	RegionLocation getRootLocation();

	void visit(UUID uuid);

	UUID getOwnerUUID();

	Location getSpawnOffset();

	void setSpawnOffset(Location location);

	default Location getSpawnLocation() {
		Location spawnLocation = this.getRootLocation().toLocation().offset(this.getSpawnOffset());
		spawnLocation.setWorld("overworld");

		return spawnLocation;
	}

	void delete();

	boolean isLegendaryCaptureShared();

	void setLegendaryCaptureShared(boolean isLegendaryCaptureShared);

	default Boolean checkPermission(@NotNull IUser user, RealmPermission permission) {
		if (user.hasPermission(CommonPermissions.ADMIN)) {
			return true;
		}

		RealmsConfig config = RealmsBackendModule.instance().getConfig();
		boolean result = checkPermission(user.getUUID(), permission);
		if (result) {
			return true;
		}

		// This is only a temporary fix because when you try to catch a pokemon, this will be triggered 4 times
		if (permission == RealmPermission.ENTITY_INTERACT) {
			return false;
		}
		String message = config.permissionMap.get(permission);
		if (message == null || message.isEmpty()) {
			return false;
		}

		user.sendMessage(message);
		return false;
	}

	Boolean checkPermission(@NotNull UUID uuid, RealmPermission permission);

	default String getServerID() {
		return new GetRealmServerRequest(this.getOwnerUUID()).sendAndGet();
	}

	default void setState(RealmState state) {
		new RealmStateChangeEvent(this.getOwnerUUID(), state).sendAndGet();
	}

	default RealmState getState() {
		return new GetRealmStateRequest(this.getOwnerUUID()).sendAndGet();
	}

	default Location getCenter() {
		double centerOffset = REGIONS_COUNT / 2.0 * 512;

		return getRootLocation().toLocation()
				.offset(centerOffset, 0, centerOffset);
	}

	default void send(IUser user) {
		user.send(
				IServerLocation.of(getServerID()),
				getSpawnLocation()
		);
	}

	IUser getOwner();

	Set<UUID> getBans();

	Boolean isBanned(UUID uuid);

	void addBan(UUID uuid);

	void removeBan(UUID uuid);

	void setMemberTrustLevel(UUID uuid, TrustLevel trustLevel);

	TrustLevel getTrustLevel(UUID uuid);

	void addMember(UUID uuid);

	void removeMember(UUID uuid);

	Boolean isMember(UUID uuid);

	Map<UUID, TrustLevel> getMembers();

	List<String> getMemberList();

	@NotNull RealmSettings getSettings();

	void setSettings(@NotNull RealmSettings settings);

	Long getDayTime();

	boolean isChunkUnlocked(ChunkLocation chunkLocation);

	void unload();
}
