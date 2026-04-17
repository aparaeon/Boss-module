package gg.mmorealms.module.tebex_integration.backend.common.gui;

import gg.mmorealms.module.analytics.common.dto.event.UserSpentAmountsRequest;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.tebex_integration.backend.common.TebexIntegrationBackendModule;
import gg.mmorealms.module.tebex_integration.backend.common.config.TebexIntegrationConfig;
import gg.mmorealms.module.tebex_integration.backend.common.dto.ClaimedMilestones;
import gg.mmorealms.module.tebex_integration.backend.common.dto.TebexMilestone;
import gg.mmorealms.module.tebex_integration.backend.common.dto.database.IUserPurchaseRewardsData;

import java.util.HashMap;
import java.util.List;

public class PurchaseRewardsGUI extends GUI {

	public static final TebexIntegrationConfig BASE_CONFIG = TebexIntegrationBackendModule.instance().getConfig();
	public static final TebexIntegrationConfig.RewardsGUI CONFIG = BASE_CONFIG.rewardsGUI;

	private final boolean isMonthly;

	public PurchaseRewardsGUI(User user, boolean isMonthly) {
		super(user, CONFIG.settings);
		this.isMonthly = isMonthly;
	}

	@Override
	public void setup() {
		setButton(CONFIG.background);

		setButton(CONFIG.back)
				.onClick((click) -> new PurchaseRewardsMainMenuGUI(user).open());

		UserSpentAmountsRequest.Response response = new UserSpentAmountsRequest(user.getUUID()).sendAndGet();

		if (response == null) {
			user.sendMessage("There was an error while fetching your purchase rewards. Please try again later.");
			close();
			return;
		}

		IUserPurchaseRewardsData userPurchaseRewardsData = IUserPurchaseRewardsData.getByUUID(user.getUUID());

		if (isMonthly) {
			ClaimedMilestones claimedMonthlyMilestones = userPurchaseRewardsData.getThisMonthClaimedMilestones();
			addMilestones(BASE_CONFIG.monthlyMilestones, CONFIG.slots, response.getTotalSpentLastMonth(), claimedMonthlyMilestones);
		} else {
			ClaimedMilestones claimedMilestones = userPurchaseRewardsData.getAllClaimedMilestones();
			addMilestones(BASE_CONFIG.alltimeMilestones, CONFIG.slots, response.getTotalSpent(), claimedMilestones);
		}
	}

	private void addMilestones(List<TebexMilestone> milestones, List<Integer> slots, double spent, ClaimedMilestones claimedMilestones) {
		for (int index = 0; index < milestones.size(); index++) {
			TebexMilestone monthlyMilestone = milestones.get(index);
			int slot = slots.get(index);

			HashMap<String, Object> placeholders = new HashMap<>() {{
				put("name", monthlyMilestone.getName());
				put("description", String.join("\n", monthlyMilestone.getDescription()));
				put("current", NumberUtils.formatNumberWithDecimalPlaces(Math.min(spent, monthlyMilestone.getThreshold()), 2));
				put("target", NumberUtils.formatNumberWithDecimalPlaces(monthlyMilestone.getThreshold(), 2));
			}};

			if (spent >= monthlyMilestone.getThreshold()) {
				if (claimedMilestones.contains(monthlyMilestone.getId())) {
					setButton(CONFIG.collectedReward, slot)
							.placeholders(placeholders);
				} else {
					setButton(CONFIG.availableReward, slot)
							.onClick((click) -> this.collectReward(monthlyMilestone))
							.placeholders(placeholders);
				}
			} else {
				setButton(CONFIG.unavailableReward, slot)
						.placeholders(placeholders);
			}
		}
	}

	private void collectReward(TebexMilestone milestone) {
		IUserPurchaseRewardsData userPurchaseRewardsData = IUserPurchaseRewardsData.getByUUID(user.getUUID());
		userPurchaseRewardsData.registerClaimedMilestone(new ClaimedMilestones.Entry(
				System.currentTimeMillis(),
				milestone.getId()
		));

		List<String> commands = milestone.getCommands()
				.parse("user", user.getUsername())
				.parse();

		for (String command : commands) {
			TebexIntegrationBackendModule.instance().executeCommand(command);
		}
		refresh();
	}

	@Override
	public String getTitleString() {
		return CONFIG.title;
	}
}
