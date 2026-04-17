package gg.mmorealms.module.hunts.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.module.hunts.backend.common.dto.HuntData;
import gg.mmorealms.module.hunts.backend.common.dto.HuntType;
import gg.mmorealms.module.hunts.backend.common.dto.database.Hunts;
import gg.mmorealms.module.hunts.backend.common.events.ActiveHuntCompletedEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.event.PokemonCapturedEvent;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import net.minecraft.server.level.ServerPlayer;

public class Listener {

    @EventHandler
    public void onPokemonCapturedEvent(PokemonCapturedEvent event) {
        ServerPlayer player = event.getPlayer();
        Hunts hunts = Hunts.get(player);

        if (!hunts.hasActiveHunt()) {
            return;
        }

        HuntType huntType = hunts.getActiveHuntType();
        HuntData huntData = hunts.getActiveHuntData();
        if (huntData == null) {
            return;
        }

        IPokemon capturedPokemon = event.getPokemon();
        if (huntData.isHuntedPokemon(capturedPokemon)) {
            new ActiveHuntCompletedEvent(hunts, huntType, player.getUUID(), capturedPokemon).fireAsync();
        }
    }

}
