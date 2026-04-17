package gg.mmorealms.module.pokemon.backend.neoforge.manager;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.utils.StringUtils;

public class PixelmonPrintUtils {
	public static String getBriefDescription(Pokemon pokemon) {
		return pokemon.getSpecies().getName() + "(" + getDetails(pokemon) + ")";
	}

	public static String getDetails(Pokemon pokemon) {
		return new MessageBuilder("{shiny}level: {level}, gender: {gender}, nature: {nature}, {ivs}")
				.parse("shiny", (pokemon.isShiny() ? "Shiny, " : ""))
				.parse("level", pokemon.getPokemonLevel())
				.parse("gender", StringUtils.toTitleCase(pokemon.getGender().name()))
				.parse("nature", StringUtils.toTitleCase(pokemon.getNature().getSerializedName()))
				.parse("ivs", getIVString(pokemon))
				.parse();
	}

	public static String getIVString(Pokemon pokemon) {
		return getIVString(pokemon.getIVs());
	}

	public static String getIVString(IVStore iVs) {
		StringBuilder ivString = new StringBuilder();
		for (BattleStatsType stat : BattleStatsType.getEVIVStatValues()) {
			if (!ivString.isEmpty()) {
				ivString.append(", ");
			}

			ivString.append(stat.name().toLowerCase())
					.append("_iv: ")
					.append(iVs.getStat(stat));
		}

		return ivString.toString();
	}
}
