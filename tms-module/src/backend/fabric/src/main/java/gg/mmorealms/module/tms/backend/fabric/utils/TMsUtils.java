package gg.mmorealms.module.tms.backend.fabric.utils;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.tms.backend.fabric.TMsFabricModule;
import gg.mmorealms.module.tms.backend.fabric.config.TMsConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TMsUtils {

	public static boolean buyTm(IUser user, Pokemon pokemon, String tmName) {
		TMsConfig config = TMsFabricModule.instance().getConfig();
		Price tmPrice = config.tmMoves.get(tmName);

		if (tmPrice == null) {
			Logger.warn("No such TM in config: " + tmName);
			return false;
		}

		String moveName = getTMMoveTemplateName(tmName);
		if (moveName == null) {
			return false;
		}

		double tmPriceAmount = tmPrice.amount();

		IBalances userBalances = IBalances.getByUser(user);
		double userCurrencyAmount = userBalances.get(tmPrice.currency());

		if (tmPriceAmount >= userCurrencyAmount) {
			user.sendMessage(config.lang.notEnoughCurrency
					.parse("currencyType", tmPrice.currency().getName()));
			return false;
		}

		userBalances.remove(tmPrice.currency(), tmPriceAmount, "TM_PURCHASE");
		user.sendMessage(config.lang.pokemonLearnedMove
				.parse("pokemonName", pokemon.getSpecies().getName())
				.parse("moveName", moveName));

		return true;
	}

	@Nullable
	public static MoveTemplate getTMMoveTemplate(String tmName) {
		MoveTemplate moveTemplate = Moves.getByName(tmName);

		if (moveTemplate == null) {
			Logger.warn("No such TM in cobblemon: " + tmName);
		}

		return moveTemplate;
	}

	@Nullable
	public static String getTMMoveTemplateName(String tmName) {
		MoveTemplate moveTemplate = getTMMoveTemplate(tmName);
		if (moveTemplate == null) {
			return null;
		}

		return getTMMoveTemplateName(moveTemplate);
	}

	public static String getTMMoveTemplateName(MoveTemplate moveTemplate) {
		return moveTemplate.getDisplayName().getString();
	}

	public static List<String> getTMsForPokemon(List<String> tms, Pokemon pokemon) {
		return tms.stream()
				.filter(tmName -> {
					MoveTemplate moveTemplate = TMsUtils.getTMMoveTemplate(tmName);
					return PokemonUtils.canPokemonLearnMove(pokemon, moveTemplate);
				})
				.sorted()
				.toList();
	}

}
