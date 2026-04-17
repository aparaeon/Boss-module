package gg.mmorealms.module.pokemon.backend.common.dto.pokemon_class;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PokemonClassSpecies {

	private PokemonClass pokemonClass;
	private List<String> species;

}
