package gg.mmorealms.module.pokemon.backend.neoforge.manager;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.PixelmonSpawnerEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.pokemon.backend.common.dto.event.PokemonCapturedEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.event.PokemonSpawnEvent;
import gg.mmorealms.module.pokemon.backend.neoforge.dto.pokemon_implementation.PixelmonPokemon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;

public class PixelmonEventManager {

    @SubscribeEvent
    public void onPokemonSpawn(PixelmonSpawnerEvent.Post event) {
        Logger.info("Pixelmon Spawn Event");
        PokemonSpawnEvent pokemonSpawnEvent = new PokemonSpawnEvent(event.getEntity());

        if (!pokemonSpawnEvent.getResult()) {
            event.getEntity().remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @SubscribeEvent
    public void onPokemonCaptured(CaptureEvent.SuccessfulCapture event) {
        Logger.info("Pixelmon Capture Event");

        ServerPlayer player = event.getPlayer();
        PixelmonPokemon pokemon = new PixelmonPokemon(event.getPokemon());
        PixelmonEntity entity = event.getPokemon().getEntity();
        new PokemonCapturedEvent(player, pokemon, entity).fireAsync();
    }
}
