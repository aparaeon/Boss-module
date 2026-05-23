package gg.mmorealms.module.login_rewards.common.dto.event;

import gg.mmorealms.loader.common.dto.event.network.NetworkBroadcast;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ClaimDailyRewardEvent extends NetworkBroadcast {

	private final UUID playerUuid;
	private final int claimDay;

}
