package gg.mmorealms.module.hunts.backend.common.events;

import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;

import java.util.UUID;

public class HuntAcceptedEvent extends HuntEvent {
    public HuntAcceptedEvent(Hunts hunts, HuntType type, UUID playerUUID) {
        super(hunts, type, playerUUID);
    }
}
