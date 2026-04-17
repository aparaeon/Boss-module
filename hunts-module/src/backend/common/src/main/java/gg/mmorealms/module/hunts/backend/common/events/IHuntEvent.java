package gg.mmorealms.module.hunts.backend.common.events;

import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IHuntEvent {
    Hunts getHunts();

    HuntType getType();

    UUID getPlayerUUID();

    @Nullable
    default ServerPlayer getPlayer() {
        return CoreBackendModule.instance()
                .getServer()
                .getPlayerList()
                .getPlayer(getPlayerUUID());
    }
}
