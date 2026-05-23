package gg.mmorealms.module.login_rewards.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import gg.mmorealms.module.login_rewards.common.dto.DailyGuiDay;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OpenDailyGUIEvent extends NetworkBroadcast {

	private final UUID playerUuid;
	private final int streak;
	private final int claimDay;
	private final long todayPlaytimeMs;
	private final long requiredPlaytimeMs;
	private final int requiredInventorySpace;
	private final boolean alreadyClaimed;
	private final boolean canClaim;
	private final boolean streakBroken;
	private final int previousStreak;
	private final long claimAvailableAtMs;
	private final long claimExpiresAtMs;
	private final List<DailyGuiDay> days;

}
