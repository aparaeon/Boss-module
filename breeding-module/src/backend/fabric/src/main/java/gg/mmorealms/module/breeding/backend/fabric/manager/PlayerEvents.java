package gg.mmorealms.module.breeding.backend.fabric.manager;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerMovedEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.module.breeding.backend.fabric.BreedingFabricModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.core.backend.fabric.mixin_interfaces.IPositionTracking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;


public class PlayerEvents {

    private PlayerEvents() {
    }

    public static void register() {
        registerGroundMovementTracking();
    }

    private static void registerGroundMovementTracking() {
        BreedingConfig config = BreedingFabricModule.instance().getConfig();
        Time updateInterval = config.egg.distanceUpdateInterval;

        ServerTickEvent.runOnTimer(PlayerEvents::sendPlayerMovementEvents, updateInterval);
    }

    private static void sendPlayerMovementEvents() {
        MinecraftServer server = BreedingFabricModule.instance().getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!(player instanceof IPositionTracking tracker)) {
                continue;
            }

            if (isPlayerFlying(player)) {
                tracker.setLastPos(null);
                continue;
            }

            updatePlayerPosition(tracker);

            double accumulatedDistance = tracker.getAccumulatedDistance();

            BreedingConfig config = BreedingFabricModule.instance().getConfig();
            double coercedDistance = Math.min(config.egg.maxDistancePerUpdate, accumulatedDistance);

            if (coercedDistance >= 1.0) {
                new PlayerMovedEvent(
                        player,
                        tracker.getCurrentPos(),
                        coercedDistance
                ).fireAsync();

                resetAccumulatedDistances(player);
            }
        }

    }

    private static boolean isPlayerRiding(ServerPlayer player) {
        return player.getVehicle() != null;
    }

    private static boolean isPlayerFlying(ServerPlayer player) {
        return player.isFallFlying() // Elytra
                || player.getAbilities().flying; // Creative flight
    }

    private static void updatePlayerPosition(IPositionTracking tracker) {
        double distance = tracker.getDistanceHorizontal();
        tracker.addAccumulatedDistance(distance);
        tracker.setLastPos(tracker.getCurrentPos());
    }

    private static void resetAccumulatedDistances(ServerPlayer player) {
        if (player instanceof IPositionTracking tracker) {
            tracker.setAccumulatedDistance(0.0);
        }
    }
}