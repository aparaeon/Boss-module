package gg.mmorealms.module.hunts.backend.common.dto;


import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import org.jetbrains.annotations.Nullable;

/**
 * Used in database to store data of available and active hunts
 */
public record HuntData(
        String speciesName,
        @Nullable String genderName,
        @Nullable String natureName,
        boolean isShiny,
        @Nullable Integer averageIVs
) {
    public boolean isHuntedPokemon(IPokemon pokemon) {
        if (pokemon == null || speciesName == null) {
            return false;
        }

        String speciesName = pokemon.getSpeciesString();
        if (speciesName == null) {
            return false;
        }

        return valueMatches(speciesName.toLowerCase(), this.speciesName.toLowerCase())
                && valueMatches(pokemon.getGenderString(), genderName)
                && valueMatches(pokemon.getNatureString(), natureName)
                && pokemon.isShiny() == isShiny
                && (averageIVs == null || pokemon.getIvsAverage() >= averageIVs);
    }

    private static <T> boolean valueMatches(T actual, @Nullable T expected) {
        Logger.debug("Actual: " + actual);
        Logger.debug("Expected: " + expected);
        return expected == null || expected.equals(actual);
    }
}