package gg.mmorealms.module.tebex_integration.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.tebex_integration.backend.common.TebexIntegrationBackendModule;
import gg.mmorealms.module.tebex_integration.backend.common.dto.ClaimedMilestones;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IUserPurchaseRewardsData extends ISavable {

	static IUserPurchaseRewardsData getByUUID(UUID uuid) {
		return TebexIntegrationBackendModule.instance().getUserPurchaseRewardsDatabaseLoader().getByIdentifier(uuid);
	}

	void registerClaimedMilestone(ClaimedMilestones.Entry milestone);

	@NotNull ClaimedMilestones getAllClaimedMilestones();

	@NotNull ClaimedMilestones getThisMonthClaimedMilestones();

}
