package gg.mmorealms.module.hunts.backend.common.dto;

import com.raduvoinea.utils.generic.RandomUtils;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.pokemon.backend.common.dto.pokemon_interfaces.IPokemon;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Used in config to define pool of possible values for {@link HuntData}
 */
public record HuntPool(
        @Nullable Time huntDuration, // null = infinite
        Time huntDenyCooldown,
        List<String> species,
        boolean pickNature,
        boolean pickGender,
        boolean isShiny,
        @Nullable Range averageIVs // null = any IVs
) {
    @Nullable
    public HuntData generateHuntData() {
        String pickedSpecies = pickRandom(species);

        IPokemon pokemon = (pickedSpecies != null)
                ? IPokemon.getBySpeciesName(pickedSpecies)
                : null;

        if (pokemon == null) {
            Logger.error("No such speciesName: " + pickedSpecies);
            return null;
        }

        String pickedGender = pickGender
                ? pickRandom(pokemon.getPossibleGenders())
                : null;

        String pickedNature = pickNature
                ? pickRandom(pokemon.getNatures())
                : null;

        Integer pickedAverageIVs = (averageIVs != null)
                ? RandomUtils.getRandom(averageIVs)
                : null;

        return new HuntData(
                pickedSpecies,
                pickedGender,
                pickedNature,
                isShiny,
                pickedAverageIVs
        );
    }

    private static <T> T pickRandom(@Nullable List<T> list) {
        return (list != null && !list.isEmpty())
                ? RandomUtils.getRandom(list)
                : null;
    }
}
