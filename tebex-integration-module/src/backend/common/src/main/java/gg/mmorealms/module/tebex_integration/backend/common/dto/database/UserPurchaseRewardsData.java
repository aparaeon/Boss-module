package gg.mmorealms.module.tebex_integration.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.utils.DateUtils;
import gg.mmorealms.module.tebex_integration.backend.common.TebexIntegrationBackendModule;
import gg.mmorealms.module.tebex_integration.backend.common.dto.ClaimedMilestones;
import gg.mmorealms.module.tebex_integration.backend.common.manager.UserPurchaseRewardsDatabaseLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(name = "user_purchase_rewards")
@Getter
@NoArgsConstructor
public class UserPurchaseRewardsData implements IDatabaseEntry<UUID>, IUserPurchaseRewardsData {

	@Id
	private UUID uuid;

	@JdbcTypeCode(SqlTypes.JSON)
	private ClaimedMilestones claimedMilestones = new ClaimedMilestones();

	public UserPurchaseRewardsData(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getIdentifier() {
		return uuid;
	}

	@Override
	public UserPurchaseRewardsDatabaseLoader getLoader() {
		return TebexIntegrationBackendModule.instance().getUserPurchaseRewardsDatabaseLoader();
	}

	public void registerClaimedMilestone(ClaimedMilestones.Entry milestone) {
		claimedMilestones.add(milestone);
	}

	public @NotNull ClaimedMilestones getAllClaimedMilestones() {
		return claimedMilestones;
	}

	public @NotNull ClaimedMilestones getThisMonthClaimedMilestones() {
		ClaimedMilestones result = new ClaimedMilestones();

		long from = DateUtils.getStartOfMonth(0);
		long to = System.currentTimeMillis();

		for (ClaimedMilestones.Entry claimedMilestone : this.claimedMilestones.getEntries()) {
			long timestamp = claimedMilestone.getTimestamp();

			if (from <= timestamp && timestamp <= to) {
				result.add(claimedMilestone);
			}
		}

		return result;
	}

}
