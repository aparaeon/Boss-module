package gg.mmorealms.module.legendaries.velocity.manager;

import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.interfaces.ILegendaryInfoManager;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LegendaryInfoManager implements ILegendaryInfoManager {
	private final Map<UUID, LegendaryInfo> info = new ConcurrentHashMap<>();
}
