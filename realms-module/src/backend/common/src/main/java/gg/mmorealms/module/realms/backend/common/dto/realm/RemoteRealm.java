package gg.mmorealms.module.realms.backend.common.dto.realm;

import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.realms.backend.common.dto.ChunkLocation;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.RealmSettings;
import gg.mmorealms.module.realms.backend.common.dto.RegionLocation;
import gg.mmorealms.module.realms.backend.common.dto.member.TrustLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RemoteRealm extends UUIDRemoteObject<IRealm> implements IRealm {

	public RemoteRealm(@NotNull UUID uuid, @NotNull String server) {
		super(IRealm.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public RegionLocation getRootLocation() {
		return sendRequest();
	}

	@Override
	public void visit(UUID uuid) {
		sendRequest(uuid);
	}

	@Override
	public UUID getOwnerUUID() {
		return getUUID();
	}

	@Override
	public Location getSpawnOffset() {
		return sendRequest();
	}

	@Override
	public void setSpawnOffset(Location location) {
		sendRequest(location);
	}

	@Override
	public void delete() {
		sendRequest();
	}

	@Override
	public boolean isLegendaryCaptureShared() {
		return sendRequest();
	}

	@Override
	public void setLegendaryCaptureShared(boolean isLegendaryCaptureShared) {
		sendRequest(isLegendaryCaptureShared);
	}

	@Override
	public Boolean checkPermission(@NotNull UUID uuid, RealmPermission permission) {
		return sendRequest(uuid, permission);
	}

	@Override
	public IUser getOwner() {
		return sendRequest();
	}

	@Override
	public Set<UUID> getBans() {
		return sendRequest();
	}

	@Override
	public Boolean isBanned(UUID uuid) {
		return sendRequest(uuid);
	}

	@Override
	public void addBan(UUID uuid) {
		sendRequest(uuid);
	}

	@Override
	public void removeBan(UUID uuid) {
		sendRequest(uuid);
	}

	@Override
	public void setMemberTrustLevel(UUID uuid, TrustLevel trustLevel) {
		sendRequest(uuid, trustLevel);
	}

	public TrustLevel getTrustLevel(UUID uuid) {
		return sendRequest(uuid);
	}

	@Override
	public void addMember(UUID uuid) {
		sendRequest(uuid);
	}

	@Override
	public void removeMember(UUID uuid) {
		sendRequest(uuid);
	}

	@Override
	public Boolean isMember(UUID uuid) {
		return sendRequest(uuid);
	}

	public Map<UUID, TrustLevel> getMembers() {
		return sendRequest();
	}

	public List<String> getMemberList() {
		return sendRequest();
	}


	public @NotNull RealmSettings getSettings() {
		return sendRequest();
	}

	public void setSettings(@NotNull RealmSettings settings) {
		sendRequest(settings);
	}

	@Override
	public Long getDayTime() {
		return sendRequest();
	}

	@Override
	public boolean isChunkUnlocked(ChunkLocation chunkLocation) {
		return sendRequest(chunkLocation);
	}

	@Override
	public void unload() {
		sendRequest();
	}
}
