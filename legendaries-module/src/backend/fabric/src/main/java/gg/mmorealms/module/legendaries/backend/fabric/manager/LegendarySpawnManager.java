package gg.mmorealms.module.legendaries.backend.fabric.manager;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.legendaries.backend.fabric.LegendariesModule;
import gg.mmorealms.module.legendaries.backend.fabric.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.event.LegendaryProxyEvent;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class LegendarySpawnManager {
    private final LegendarySpawnConfig config;
    private final LegendarySpawner spawner;
    private final LegendaryInfoManager infoManager;

    public LegendarySpawnManager(LegendaryInfoManager infoManager) {
        this.config = LegendariesModule.instance().getConfig();
        this.spawner = new LegendarySpawner();
        this.infoManager = infoManager;
    }

    public void attemptLegendarySpawn(@Nullable UUID senderUUID) {
        spawner.attemptLegendarySpawnAsync()
                .thenAcceptAsync(pokemonEntity -> {
                    if (pokemonEntity == null) {
                        Logger.warn("Failed to spawn legendary Pokémon");
                        if (senderUUID != null) {
                            IUser.getByUUID(senderUUID).sendMessage(config.lang.failedLegendarySpawn);
                        }
                        LegendaryProxyEvent.failed().send();
                        return;
                    }

                    LegendaryInfo info = infoManager.add(pokemonEntity);
                    LegendaryProxyEvent.spawned(info).send();

                    Logger.good("Successfully spawned legendary " +
                            info.getPokemonName() + " at " + info.getLocation().toString() +
                            " UUID " + info.getPokemonUUID());
                })
                .exceptionally(e -> {
                    Logger.error("Exception during legendary spawn: " + e.getMessage());
                    if (senderUUID != null) {
                        IUser.getByUUID(senderUUID).sendMessage(config.lang.failedLegendarySpawn);
                    }
                    return null;
                });
    }
}