package gg.mmorealms.module.pokemon.backend.fabric.manager;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.common.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

public class CobblemonPrintUtils {
	public static String getBriefDescription(Pokemon pokemon) {
		return pokemon.getSpecies().getName() + "(" + getDetails(pokemon) + ")";
	}

	public static String getDetails(Pokemon pokemon) {
		return new MessageBuilder("{shiny}level: {level}, gender: {gender}, nature: {nature}, {ivs}")
				.parse("shiny", (pokemon.getShiny() ? "Shiny, " : ""))
				.parse("level", pokemon.getLevel())
				.parse("gender", StringUtils.toTitleCase(pokemon.getGender().getSerializedName()))
				.parse("nature", pokemon.getNature().getName().getPath())
				.parse("ivs", getIVString(pokemon))
				.parse();
	}

	public static String getIVString(Pokemon pokemon) {
		return getIVString(pokemon.getIvs());
	}

	public static String getIVString(IVs iVs) {
		StringBuilder ivString = new StringBuilder();
		for (@NotNull Stats stat : Stats.getEntries()) {
			if (iVs.get(stat) == null) {
				continue;
			}

			if (!ivString.isEmpty()) {
				ivString.append(", ");
			}


			ivString.append(stat.name().toLowerCase())
					.append("_iv: ")
					.append(iVs.get(stat));
		}

		return ivString.toString();
	}
}
