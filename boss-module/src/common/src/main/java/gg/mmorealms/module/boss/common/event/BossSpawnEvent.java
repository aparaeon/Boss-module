package gg.mmorealms.module.boss.common.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkEvent;
import gg.mmorealms.module.boss.common.BossTier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
@Getter
public class BossSpawnEvent extends NetworkEvent {
	private final BossTier tier;
	private final @Nullable String species;
	private final boolean shiny;
	private final @Nullable Integer level;
	private final @Nullable UUID requesterUUID;
	private final @NotNull List<UUID> eligiblePlayerUUIDs;

	public BossSpawnEvent(
			@NotNull String targetServerId,
			@NotNull BossTier tier,
			@Nullable String species,
			boolean shiny,
			@Nullable Integer level,
			@Nullable UUID requesterUUID,
			@NotNull List<UUID> eligiblePlayerUUIDs) {
		super(targetServerId);
		this.tier = tier;
		this.species = species;
		this.shiny = shiny;
		this.level = level;
		this.requesterUUID = requesterUUID;
		this.eligiblePlayerUUIDs = eligiblePlayerUUIDs;
	}
}
