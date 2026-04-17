package gg.mmorealms.module.hunts.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteHunts extends UUIDRemoteObject<IHunts> implements IHunts {

    public RemoteHunts(@NotNull UUID uuid, @NotNull String server) {
        super(IHunts.class, uuid, server);
    }

    @Override
    public boolean hasHuntOnCooldown() {
        return sendRequest();
    }

    @Override
    public boolean isHuntOnCooldown(HuntType type) {
        return sendRequest(type);
    }

    @Override
    public boolean isHuntCooldownExpired(HuntType type) {
        return sendRequest(type);
    }

    @Override
    public boolean hasActiveHunt() {
        return sendRequest();
    }

    @Override
    public boolean isActiveHunt(HuntType type) {
        return sendRequest(type);
    }

    @Override
    public boolean isActiveHuntExpired() {
        return sendRequest();
    }

    @Override
    public long getActiveHuntDurationLeft() {
        return sendRequest();
    }

    @Override
    public void completeActiveHunt() {
        sendRequest();
    }

    @Override
    public void clearActiveHunt() {
        sendRequest();
    }

    @Override
    public boolean canAcceptHunt(HuntType type) {
        return sendRequest(type);
    }

    @Override
    public void acceptHunt(HuntType type) {
        sendRequest(type);
    }

    @Override
    public boolean canDenyHunt(HuntType type) {
        return sendRequest(type);
    }

    @Override
    public String getHuntDenyCooldownFormattedTime(HuntType type) {
        return sendRequest(type);
    }

    @Override
    public void denyHunt(HuntType type) {
        sendRequest(type);
    }

    @Override
    public void refreshHunts() {
        sendRequest();
    }

    @Override
    public void save() {
        sendRequest();
    }
}
