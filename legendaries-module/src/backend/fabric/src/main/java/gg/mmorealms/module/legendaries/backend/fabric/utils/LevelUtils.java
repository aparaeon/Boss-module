package gg.mmorealms.module.legendaries.backend.fabric.utils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.legendaries.backend.fabric.LegendariesModule;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class LevelUtils {

	@Nullable
	public static PokemonEntity findLegendary(LegendaryInfo info) {
		return findLegendary(info.getPokemonEntityUUID(), info.getLevelName());
	}

	@Nullable
	public static PokemonEntity findLegendary(UUID uuid, String levelName) {
		ServerLevel level = getLevel(levelName);
		if (level == null) {
			return null;
		}

		Entity entity = level.getEntity(uuid);
		if (entity == null) {
			Logger.debug("Couldn't find loaded entity.");
			return null;
		}

		if (entity instanceof PokemonEntity pokemonEntity) {
			return pokemonEntity;
		} else {
			Logger.debug("Found entity is not instanceof PokemonEntity.");
		}

		return null;
	}

	public static @Nullable ServerLevel getLevel(String name) {
		return LegendariesModule.instance()
				.getServer()
				.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(name)));
	}
}