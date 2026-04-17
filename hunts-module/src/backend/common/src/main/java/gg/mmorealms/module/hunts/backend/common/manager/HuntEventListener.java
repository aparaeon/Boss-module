package gg.mmorealms.module.hunts.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import gg.mmorealms.module.hunts.backend.common.events.ActiveHuntCompletedEvent;
import gg.mmorealms.module.hunts.backend.common.events.ActiveHuntExpiredEvent;
import gg.mmorealms.module.hunts.backend.common.events.HuntCooldownExpiredEvent;
import gg.mmorealms.module.hunts.backend.common.utils.HuntUtils;
import net.minecraft.server.level.ServerPlayer;

public class HuntEventListener {

    public HuntEventListener() { }

    @EventHandler
    public void onActiveHuntCompletedEvent(ActiveHuntCompletedEvent event) {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        ServerPlayer player = event.getPlayer();
        if (player == null) {
            return;
        }

        Hunts hunts = event.getHunts();
        HuntType type = event.getType();

        HuntUtils.awardPlayer(type, player);
        hunts.completeActiveHunt();

        User.get(player).sendMessage(config.lang.huntCompleted);
    }

    // Announce failed Hunt
    @EventHandler
    public void onActiveHuntExpiredEvent(ActiveHuntExpiredEvent event) {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        ServerPlayer player = event.getPlayer();
        if (player == null) {
            return;
        }

        User.get(player).sendMessage(config.lang.huntFailed);
    }

    // Announce new Hunt
    @EventHandler
    public void onHuntCooldownExpiredEvent(HuntCooldownExpiredEvent event) {
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        ServerPlayer player = event.getPlayer();
        if (player == null) {
            return;
        }

        MessageBuilder message = config.lang.huntGenerated.parse("type", event.getType().getFriendlyName());
        User.get(player).sendMessage(message);
    }

}
