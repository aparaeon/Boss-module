package gg.mmorealms.module.legendaries.backend.fabric.manager;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.CancelableTimeTask;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.legendaries.backend.fabric.utils.LevelUtils;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.event.LegendaryProxyEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LegendaryDespawnManager {
    private final LegendaryInfoManager infoManager;

    private final Map<UUID, CancelableTimeTask> scheduledTasks = new ConcurrentHashMap<>();

    public LegendaryDespawnManager(LegendaryInfoManager infoManager) {
        this.infoManager = infoManager;
        registerEventListener();
    }

    private void registerEventListener() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof PokemonEntity pokemonEntity) {
                UUID uuid = pokemonEntity.getPokemon().getUuid();

                if (infoManager.getFailedDespawns().contains(uuid)) {
                    discard(pokemonEntity);
                }
            }
        });
    }

    public void despawnLegendary(UUID pokemonUUID) {
        if (scheduledTasks.containsKey(pokemonUUID)) {
            Logger.warn("Despawn already scheduled for " + pokemonUUID);
            return;
        }

        LegendaryInfo info = infoManager.get(pokemonUUID);
        if (info == null) {
            Logger.warn("No legendary info stored for: " + pokemonUUID);
            return;
        }

        infoManager.getFailedDespawns().add(pokemonUUID);
        scheduleChunkedDespawn(info);
    }

    private void scheduleChunkedDespawn(LegendaryInfo info) {
        PokemonEntity loaded = LevelUtils.findLegendary(info);

        if (loaded != null) {
            if (loaded.isBusy()) {
                Logger.warn("Found Legendary is busy, waiting to despawn: " + info.getPokemonUUID());
                waitUntilNotBusy(loaded, info);
            } else {
                despawn(loaded, info);
            }
        } else {
            Logger.warn("No legendary found for despawn: " + info.getPokemonUUID());
            LegendaryProxyEvent.despawned(info).send();
        }
        // Skipped chunk loading (caused crashes).
    }

    private void waitUntilNotBusy(PokemonEntity entity, LegendaryInfo info) {
        CancelableTimeTask task = ScheduleUtils.runTaskTimer(() -> {
            if (!entity.isBusy()) {
                despawn(entity, info);
            }
        }, Time.seconds(5));

        scheduledTasks.put(info.getPokemonUUID(), task);
    }

    public void despawn(PokemonEntity entity, LegendaryInfo info) {
        discard(entity);
        LegendaryProxyEvent.despawned(info).send();
    }

    public void discard(PokemonEntity entity) {
        Pokemon pokemon = entity.getPokemon();
        UUID pokemonUUID = pokemon.getUuid();

        Logger.good("Successfully despawned " + pokemon.getSpecies().getName() + " with UUID " + pokemonUUID);
        entity.discard();
        cleanState(pokemonUUID);
    }

    public void cleanState(UUID pokemonUUID) {
        CancelableTimeTask task = scheduledTasks.remove(pokemonUUID);
        if (task != null) {
            task.cancel();
        }

        infoManager.getFailedDespawns().remove(pokemonUUID);
        infoManager.remove(pokemonUUID);
    }
}