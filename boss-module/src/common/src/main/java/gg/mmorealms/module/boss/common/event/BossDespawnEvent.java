package gg.mmorealms.module.boss.common.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.boss.common.BossTier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class BossDespawnEvent extends NetworkEvent {
	private final @Nullable UUID pokemonUUID;
	private final @Nullable BossTier tierFilter;
	private final boolean despawnAll;

	public BossDespawnEvent(
			@NotNull String targetServerId,
			@Nullable UUID pokemonUUID,
			@Nullable BossTier tierFilter,
			boolean despawnAll
	) {
		super(targetServerId);
		this.pokemonUUID = pokemonUUID;
		this.tierFilter = tierFilter;
		this.despawnAll = despawnAll;
	}
}
