package gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class;

import com.raduvoinea.utils.generic.RandomUtils;
import gg.mmorealms.module.pokemon.backend.common.PokemonBackendModule;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum PokemonClass {
	MYTHICAL,
	NORMAL,
	LEGENDARY,
	ULTRA_BEAST;

	public static IPokemon parse(String pokemonClassString, String shinyString) {
		try {
			PokemonClass pokemonClass = PokemonClass.valueOf(pokemonClassString.toUpperCase());
			boolean shiny = Boolean.parseBoolean(shinyString);
			return pokemonClass.getRandomPokemon(shiny);
		} catch (Exception e) {
			return null;
		}
	}

	public @Nullable IPokemon getRandomPokemon(boolean shiny) {
		List<String> speciesList = PokemonBackendModule.instance().getConfig().pokemonClasses.get(this);
		String pokemonSpeciesName = RandomUtils.getRandom(speciesList);

		return IPokemon.getBySpeciesName(pokemonSpeciesName, shiny);
	}


}
