package gg.mmorealms.module.chat_games.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import gg.mmorealms.module.chat_games.common.dto.LeaderboardEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OpenLeaderboardEvent extends NetworkBroadcast {

	private UUID playerUuid;
	private List<LeaderboardEntry> overallEntries;
	private List<LeaderboardEntry> seasonEntries;
	private boolean season;
	private Map<Integer, List<String>> seasonRewardLore;

	public OpenLeaderboardEvent(UUID playerUuid, List<LeaderboardEntry> overallEntries,
	                            List<LeaderboardEntry> seasonEntries, boolean season,
	                            Map<Integer, List<String>> seasonRewardLore) {
		super();
		this.playerUuid = playerUuid;
		this.overallEntries = overallEntries;
		this.seasonEntries = seasonEntries;
		this.season = season;
		this.seasonRewardLore = seasonRewardLore;
	}

}
