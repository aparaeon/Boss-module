package gg.mmorealms.module.hunts.backend.common.dto.database;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.HuntData;
import gg.mmorealms.module.hunts.backend.common.dto.HuntPool;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.events.ActiveHuntExpiredEvent;
import gg.mmorealms.module.hunts.backend.common.events.HuntAcceptedEvent;
import gg.mmorealms.module.hunts.backend.common.events.HuntCooldownExpiredEvent;
import gg.mmorealms.module.hunts.backend.common.events.HuntDeniedEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;
import org.hibernate.Session;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

@Entity(name = "hunts")
@NoArgsConstructor
@Getter
@Setter
public class Hunts implements IHunts, IDatabaseEntry<UUID> {

    @Id
    @NotNull
    private UUID uuid;

    // Active
    @Nullable
    @JdbcTypeCode(SqlTypes.JSON)
    private HuntData activeHuntData;

    @Nullable
    @Enumerated(EnumType.STRING)
    private HuntType activeHuntType;

    private static final String ACTIVE_KEY = "active_";

    public Hunts(UUID uuid) {
        this.uuid = uuid;
    }

    public Hunts(ServerPlayer player) {
        this(player.getUUID());
    }

    /* ---------- Hunt Cooldown ---------- */

    public boolean hasHuntOnCooldown() {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        for (HuntType type : config.huntPools.keySet()) {
            if (cooldowns().isPresent(type.name())) {
                return true;
            }
        }

        return false;
    }

    public boolean isHuntOnCooldown(HuntType type) {
        return cooldowns().isActive(type.name());
    }

    public boolean isHuntCooldownExpired(HuntType type) {
        return isHuntCooldownExpired(type.name());
    }

    private boolean isHuntCooldownExpired(String type) {
        if (!cooldowns().isPresent(type)) {
            return false; // Key not present = not expired
        }

        return cooldowns().hasExpired(type);
    }

    private void removeCooldown(HuntType type) {
        removeCooldown(type.name());
    }

    private void removeCooldown(String type) {
        cooldowns().remove(type);
    }

    /* ---------- Active Hunt ---------- */

    public boolean hasActiveHunt() {
        return activeHuntData != null && activeHuntType != null;
    }

    public boolean isActiveHunt(HuntType type) {
        return hasActiveHunt() && activeHuntType == type;
    }

    public boolean isActiveHuntExpired() {
        return hasActiveHunt() && isHuntCooldownExpired(activeHuntKey());
    }

    public long getActiveHuntDurationLeft() {
        String key = activeHuntKey();
        if (key == null) {
            return Long.MAX_VALUE;
        }

        return cooldowns().getRemaining(key);
    }

    public void completeActiveHunt() {
        if (hasActiveHunt()) {
            clearActiveHunt();
        }
    }

    public void clearActiveHunt() {
        activeHuntData = null;
        activeHuntType = null;
        removeCooldown(activeHuntKey());

        try {
            save();
        } catch (DatabaseSaveException e) {
            Logger.error(new MessageBuilder("{uuid} tried to clear active hunt but there was a save exception")
                    .parse("uuid", uuid));
        }
    }


    /* ---------- Accept ---------- */

    public boolean canAcceptHunt(HuntType type) {
        return !hasActiveHunt() && !isHuntOnCooldown(type);
    }

    public void acceptHunt(HuntType type) {
        if (!canAcceptHunt(type)) {
            return;
        }

        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        HuntPool huntPool = config.huntPools.get(type);
        Time huntDuration = huntPool.huntDuration();

        HuntData huntData = generateHuntData(huntPool);
        if (huntData == null) {
            Logger.error("Generated null HuntData");
            return;
        }

        activeHuntData = generateHuntData(huntPool);
        activeHuntType = type;

        if (huntDuration != null) {
            cooldowns().set(activeHuntKey(), huntDuration);
        } else {
            // To be sure that there is no previous active hunt key leftover by some bug
            removeCooldown(activeHuntKey());
        }

        try {
            save();
        } catch (DatabaseSaveException e) {
            Logger.error(new MessageBuilder("{uuid} tried to accept {huntType} type but there was a save exception")
                    .parse("uuid", uuid)
                    .parse("huntType", type.getFriendlyName()));
            return;
        }

        new HuntAcceptedEvent(this, type, uuid).fireAsync();
    }

    /* ---------- Deny ---------- */

    public boolean canDenyHunt(HuntType type) {
        return isActiveHunt(type);
    }

    public String getHuntDenyCooldownFormattedTime(HuntType type) {
        return cooldowns().getFormattedTime(type.name());
    }

    public void denyHunt(HuntType type) {
        if (!canDenyHunt(type)) {
            return;
        }

        clearActiveHunt();

        HuntsConfig config = HuntsBackendModule.instance().getConfig();
        HuntPool huntPool = config.huntPools.get(type);

        Time huntDenyCooldown = huntPool.huntDenyCooldown();
        if (huntDenyCooldown == null) {
            return;
        }

        cooldowns().set(type.name(), huntDenyCooldown);

        new HuntDeniedEvent(this, type, uuid).fireAsync();
    }

    /* ---------- Populate ---------- */

    public void refreshHunts() {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        for (Map.Entry<HuntType, HuntPool> entry : config.huntPools.entrySet()) {

            HuntType type = entry.getKey();

            if (isActiveHunt(type) && isActiveHuntExpired()) {
                // If this is an active hunt and expired
                new ActiveHuntExpiredEvent(this, type, uuid).fireAsync();
                clearActiveHunt();
            } else if (isHuntCooldownExpired(type)) {
                // If this has cooldown and expired
                new HuntCooldownExpiredEvent(this, type, uuid).fireAsync();
                removeCooldown(type);
            }

        }
    }

    @Nullable
    private HuntData generateHuntData(HuntPool huntPool) {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();
        HuntData huntData = huntPool.generateHuntData();

        if (huntData == null) {
            IUser user = IUser.getByUUID(uuid);
            user.sendMessage(config.lang.huntAcceptingError);
            return null;
        }

        return huntData;
    }

    @Nullable
    private String activeHuntKey() {
        if (activeHuntType == null) {
            return null;
        }

        return ACTIVE_KEY + activeHuntType.name();
    }

    private IBackendCooldowns cooldowns() {
        return IBackendCooldowns.getByUUID(uuid);
    }

    /* ---------- Static ---------- */

    public static @NotNull Hunts get(IUser user) {
        return get(user.getUUID());
    }

    public static Hunts get(ServerPlayer player) {
        return get(player.getUUID());
    }

    public static Hunts get(UUID uuid) {
        if(CommonLoader.DUMMY_MODE){
            Logger.warn("Trying to get Hunts for uuid " + uuid + " while in dummy mode, returning new instance");
            return new Hunts(uuid);
        }

        try (Session session = HuntsBackendModule.instance()
                .getDatabaseManager()
                .getSessionFactory()
                .openSession()) {

            Hunts hunts = session.get(Hunts.class, uuid);
            if (hunts == null) {
                return new Hunts(uuid);
            }

            return hunts;
        }
    }

    @Override
    public UUID getIdentifier() {
        return uuid;
    }

    @Override
    public SyncedDatabaseLoader<UUID, ?, ?, ?> getLoader() {
        return HuntsBackendModule.instance().getHuntsDatabaseLoader();
    }
}