package gg.mmorealms.module.legendaries.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class LegendaryDespawnEvent extends NetworkEvent {
	private final UUID pokemonUUID;

	public LegendaryDespawnEvent(@NotNull String targetServerId, @NotNull UUID pokemonUUID) {
		super(targetServerId);
		this.pokemonUUID = pokemonUUID;
	}
}
