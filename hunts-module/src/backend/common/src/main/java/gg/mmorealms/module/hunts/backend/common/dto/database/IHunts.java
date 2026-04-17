package gg.mmorealms.module.hunts.backend.common.dto.database;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import jakarta.validation.constraints.NotNull;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface IHunts extends ISavable {

    static @NotNull IHunts get(IUser user) {
        return get(user.getUUID());
    }

    static IHunts get(ServerPlayer player) {
        return get(player.getUUID());
    }

    static IHunts get(UUID uuid) {
        IHunts hunts = HuntsBackendModule.instance().getHuntsDatabaseLoader().getByIdentifier(uuid);

        if (hunts == null) {
            Logger.error("Failed to automatically create Hunts for user with UUID: " + uuid);
            return new Hunts(uuid);
        }

        return hunts;
    }

    boolean hasHuntOnCooldown();

    boolean isHuntOnCooldown(HuntType type);

    boolean isHuntCooldownExpired(HuntType type);

    boolean hasActiveHunt();

    boolean isActiveHunt(HuntType type);

    boolean isActiveHuntExpired();

    long getActiveHuntDurationLeft();

    void completeActiveHunt();

    void clearActiveHunt();

    boolean canAcceptHunt(HuntType type);

    void acceptHunt(HuntType type);

    boolean canDenyHunt(HuntType type);

    String getHuntDenyCooldownFormattedTime(HuntType type);

    void denyHunt(HuntType type);

    void refreshHunts();

}
