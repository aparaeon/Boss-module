package gg.mmorealms.module.pokedex_rewards.backend.fabric.manager;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.pokemon.backend.fabric.dto.event.PrePokedexDataChanged;

import java.util.UUID;

public class Listener {

	@EventHandler
	public void onPrePokedexDataChanged(PrePokedexDataChanged event) {
		if (!event.getResult()) {
			return;
		}

		if (!event.getKnowledge().equals(PokedexEntryProgress.CAUGHT)) {
			event.setResult(false);
			return;
		}

		String originalTrainerString = event.getDataSource().getPokemon().getOriginalTrainer();
		UUID originalTrainerUUID;

		if (originalTrainerString == null) {
			return;
		}

		try {
			originalTrainerUUID = UUID.fromString(originalTrainerString);
		} catch (IllegalArgumentException exception) {
			Logger.log("Invalid UUID format for original trainer: " + originalTrainerString);
			Logger.error(exception);
			return;
		}

		if (originalTrainerUUID.equals(event.getPlayerUUID())) {
			return;
		}

		event.setResult(false);
	}

}
