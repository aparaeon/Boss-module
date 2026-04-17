package gg.mmorealms.module.hunts.backend.common.manager;

import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.module.hunts.backend.common.HuntsBackendModule;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import net.minecraft.server.MinecraftServer;

public class HuntEvents {

    private HuntEvents() { }

    public static void register() {
        registerHuntExpirationTracking();
    }

    private static void registerHuntExpirationTracking() {
        MinecraftServer server = HuntsBackendModule.instance().getServer();
        HuntsConfig config = HuntsBackendModule.instance().getConfig();

        ServerTickEvent.runOnTimer(() -> {
            server.getPlayerList().getPlayers().forEach(player -> {
                Hunts hunts = Hunts.get(player);
                hunts.refreshHunts();
            });
        }, config.huntsRefreshInterval);
    }

}
