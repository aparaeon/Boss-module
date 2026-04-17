package gg.mmorealms.module.catch_combo.backend.fabric.utils;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.StreamSupport;

public class PokemonUtils {

    @Nullable
    public static Pokemon getWildPokemon(PokemonBattle battle) {
        return StreamSupport.stream(battle.getActivePokemon().spliterator(), false)
                .map(ActiveBattlePokemon::getBattlePokemon)
                .filter(Objects::nonNull)
                .map(BattlePokemon::getOriginalPokemon)
                .filter(pokemon -> !pokemon.isPlayerOwned())
                .findFirst()
                .orElse(null);
    }

}
