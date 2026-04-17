package gg.mmorealms.module.tms.backend.fabric.utils;

import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;

public class PokemonUtils {

	private PokemonUtils() {
	}

	public static boolean canPokemonLearnMove(Pokemon pokemon, MoveTemplate moveTemplate) {
		return !isBattling(pokemon)
				&& !isPokemonKnowsMove(pokemon, moveTemplate)
				&& isPokemonLearnsetContainsTMMove(pokemon, moveTemplate);
	}

	public static boolean isBattling(Pokemon pokemon) {
		PokemonEntity pokemonEntity = pokemon.getEntity();
		return pokemonEntity != null && pokemonEntity.isBattling();
	}

	public static boolean isPokemonKnowsMove(Pokemon pokemon, MoveTemplate moveTemplate) {
		boolean isMoveSetMove = pokemon.getMoveSet().getMoves().stream()
				.anyMatch(move -> move.getTemplate().equals(moveTemplate));

		boolean isAccessibleMove = pokemon.getAllAccessibleMoves().stream()
				.anyMatch(accessibleMoveTemplate -> accessibleMoveTemplate.equals(moveTemplate));

		return isMoveSetMove || isAccessibleMove;
	}

	public static boolean isPokemonLearnsetContainsTMMove(Pokemon pokemon, MoveTemplate moveTemplate) {
		return pokemon.getForm().getMoves().getTmMoves().contains(moveTemplate);
	}

	public static void teachPokemonMove(Pokemon pokemon, MoveTemplate moveTemplate) {
		if (pokemon.getMoveSet().hasSpace()) {
			pokemon.getMoveSet().add(moveTemplate.create());
		} else {
			pokemon.getBenchedMoves().add(new BenchedMove(moveTemplate, 0));
		}
	}
}
