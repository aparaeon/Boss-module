package gg.mmorealms.module.tebex_integration.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import gg.mmorealms.module.tebex_integration.backend.common.dto.ClaimedMilestones;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteUserPurchaseRewardsData extends UUIDRemoteObject<UserPurchaseRewardsData> implements IUserPurchaseRewardsData {
	public RemoteUserPurchaseRewardsData(@NotNull UUID uuid, @NotNull String server) {
		super(UserPurchaseRewardsData.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	@Override
	public void registerClaimedMilestone(ClaimedMilestones.Entry milestone) {
		sendRequest(milestone);
	}

	@Override
	public @NotNull ClaimedMilestones getAllClaimedMilestones() {
		return sendRequest();
	}

	@Override
	public @NotNull ClaimedMilestones getThisMonthClaimedMilestones() {
		return sendRequest();
	}
}
