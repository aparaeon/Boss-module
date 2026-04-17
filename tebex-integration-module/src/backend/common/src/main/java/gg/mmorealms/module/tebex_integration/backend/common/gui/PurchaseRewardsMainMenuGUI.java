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

public class PurchaseRewardsMainMenuGUI extends GUI {

	public static final TebexIntegrationConfig BASE_CONFIG = TebexIntegrationBackendModule.instance().getConfig();
	public static final TebexIntegrationConfig.RewardsMainMenuGUI CONFIG = BASE_CONFIG.rewardsMainMenuGUI;

	public PurchaseRewardsMainMenuGUI(User user) {
		super(user, CONFIG.settings);
	}

	@Override
	public void setup() {
		setButton(CONFIG.background);

		setButton(CONFIG.purchaseMonthlyRewards)
				.onClick((click)-> new PurchaseRewardsGUI(user, true).open());
		setButton(CONFIG.purchaseAllTimeRewards)
				.onClick((click)-> new PurchaseRewardsGUI(user, false).open());
	}

	@Override
	public String getTitleString() {
		return CONFIG.title;
	}
}
