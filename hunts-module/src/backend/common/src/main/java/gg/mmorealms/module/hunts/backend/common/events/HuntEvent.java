package gg.mmorealms.module.hunts.backend.common.events;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class HuntEvent extends NetworkBroadcast implements IHuntEvent {
    private final Hunts hunts;
    private final HuntType type;
    private final UUID playerUUID;
}
