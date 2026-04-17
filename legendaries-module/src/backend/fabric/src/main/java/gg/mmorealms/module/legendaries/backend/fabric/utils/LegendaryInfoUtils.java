package gg.mmorealms.module.legendaries.backend.fabric.utils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.legendaries.backend.fabric.LegendariesModule;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LegendaryInfoUtils {

	private LegendaryInfoUtils() { }

	@SuppressWarnings("resource")
	public static LegendaryInfo createSpawnInfo(PokemonEntity entity) {
		BlockPos pos = entity.getOnPos().above();

		// Get parsed biome name
		// We cant use same method for dimensions, as there are no language keys for them.
		ResourceLocation biomeResource = ResourceLocation.parse(entity.level().getBiome(pos).getRegisteredName());
		String biomeLanguageKey = biomeResource.toLanguageKey("biome");
		String biome = Component.translatable(biomeLanguageKey).getString();

		return new LegendaryInfo(
				entity.getPokemon().getSpecies().getName(),
				entity.getUUID(),
				entity.getPokemon().getUuid(),
				LegendariesModule.instance().getServerID(),
				entity.level().dimension().location().toString(),
				biome,
				LocationUtils.blockPosToLocation(pos),
				System.currentTimeMillis(),
				null
		);
	}
}
