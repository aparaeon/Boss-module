package gg.mmorealms.module.chat_games.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OpenRewardClaimEvent extends NetworkBroadcast {

	private UUID playerUuid;
	private int seasonId;
	private int placement;
	private long wins;
	private int requiredSlots;
	private List<String> displayItems;
	private List<String> commands;
	private List<String> rewardLore;

	public OpenRewardClaimEvent(UUID playerUuid, int seasonId, int placement, long wins,
	                            int requiredSlots, List<String> displayItems, List<String> commands,
	                            List<String> rewardLore) {
		this.playerUuid = playerUuid;
		this.seasonId = seasonId;
		this.placement = placement;
		this.wins = wins;
		this.requiredSlots = requiredSlots;
		this.displayItems = displayItems;
		this.commands = commands;
		this.rewardLore = rewardLore;
	}

}
