package gg.mmorealms.module.legendaries.backend.fabric.manager;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import gg.mmorealms.module.legendaries.backend.fabric.utils.LegendaryInfoUtils;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.interfaces.ILegendaryInfoManager;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LegendaryInfoManager implements ILegendaryInfoManager {
	private final Map<UUID, LegendaryInfo> info = new ConcurrentHashMap<>();
	private final Set<UUID> failedDespawns = ConcurrentHashMap.newKeySet();

	public LegendaryInfo add(PokemonEntity entity) {
		LegendaryInfo info = LegendaryInfoUtils.createSpawnInfo(entity);
		add(info);
		return info;
	}

	public boolean isActiveLegendary(PokemonEntity pokemonEntity) {
		return isActiveLegendary(pokemonEntity.getPokemon());
	}

	public boolean isActiveLegendary(Pokemon pokemon) {
		return isActiveLegendary(pokemon.getUuid());
	}

}
