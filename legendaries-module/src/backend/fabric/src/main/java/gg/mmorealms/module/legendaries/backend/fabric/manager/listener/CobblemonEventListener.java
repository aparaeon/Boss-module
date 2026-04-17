package gg.mmorealms.module.legendaries.backend.fabric.manager.listener;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.legendaries.backend.fabric.manager.LegendaryDespawnManager;
import gg.mmorealms.module.legendaries.backend.fabric.manager.LegendaryInfoManager;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.event.LegendaryProxyEvent;
import kotlin.Unit;

import java.util.UUID;

public class CobblemonEventListener {
	private final LegendaryInfoManager infoManager;
	private final LegendaryDespawnManager despawnManager;

	public CobblemonEventListener(LegendaryInfoManager infoManager, LegendaryDespawnManager despawnManager) {
		this.infoManager = infoManager;
		this.despawnManager = despawnManager;
		setupCobblemonEventListeners();
	}

	private void setupCobblemonEventListeners() {
		// Accounts for both in-battle faint and entity being killed
		CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, event -> {
			Pokemon pokemon = event.getPokemon();

			if (infoManager.isActiveLegendary(pokemon)) {
				UUID pokemonUUID = pokemon.getUuid();
				LegendaryInfo info = infoManager.get(pokemonUUID);

				LegendaryProxyEvent.fainted(info).send();
				despawnManager.cleanState(pokemonUUID);
			}
			return Unit.INSTANCE;
		});

		CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
			Pokemon pokemon = event.getPokemon();

			if (infoManager.isActiveLegendary(pokemon)) {
				UUID pokemonUUID = pokemon.getUuid();
				LegendaryInfo info = infoManager.get(pokemonUUID);

				if (info != null) {
					info.setInteractedPlayerName(event.getPlayer().getName().getString());
				}

				LegendaryProxyEvent.captured(info).send();
				despawnManager.cleanState(pokemonUUID);
			}
			return Unit.INSTANCE;
		});
	}
}
