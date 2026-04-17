package gg.mmorealms.module.legendaries.common.interfaces;

import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILegendaryInfoManager {

	Map<UUID, LegendaryInfo> getInfo();

	default void add(LegendaryInfo info) {
		getInfo().put(info.getPokemonUUID(), info);
	}

	@Nullable
	default LegendaryInfo get(UUID uuid) {
		return getInfo().get(uuid);
	}

	default void remove(UUID uuid) {
		getInfo().remove(uuid);
	}

	default List<LegendaryInfo> getAll() {
		return new ArrayList<>(getInfo().values());
	}

	default boolean isActiveLegendary(UUID uuid) {
		return getInfo().containsKey(uuid);
	}

	default void clear() {
		getInfo().clear();
	}

}
