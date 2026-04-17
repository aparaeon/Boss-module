package gg.mmorealms.module.legendaries.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class LegendarySpawnEvent extends NetworkEvent {
	private final UUID senderUUID;

	public LegendarySpawnEvent(@NotNull String targetServerId) {
		this(targetServerId, null);
	}

	public LegendarySpawnEvent(@NotNull String targetServerId, @Nullable UUID senderUUID) {
		super(targetServerId);
		this.senderUUID = senderUUID;
	}
}
