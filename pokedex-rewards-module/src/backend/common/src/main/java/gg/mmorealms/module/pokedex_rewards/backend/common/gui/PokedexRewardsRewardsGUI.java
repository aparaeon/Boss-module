package gg.mmorealms.module.pokedex_rewards.backend.common.gui;


import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.pokedex_rewards.backend.common.PokedexRewardsBackendModule;
import gg.mmorealms.module.pokedex_rewards.backend.common.config.PokedexRewardsConfig;

public class PokedexRewardsRewardsGUI extends GUI {

	private final static PokedexRewardsConfig CONFIG = PokedexRewardsBackendModule.instance().getConfig();

	public PokedexRewardsRewardsGUI(User user) {
		super(user, CONFIG.gui.settings);
	}

	@Override
	public String getTitleString() {
		return CONFIG.guiHeader;
	}

	@Override
	public void setup() {
		setupBackgroundButtons();
		setupRewardButtons();
	}

	private void setupRewardButtons() {
		CONFIG.gui.rewards.forEach(item ->
				setButton(item.toGUIButton(user, this::refreshOnClick))
		);
	}

	private void setupBackgroundButtons() {
		CONFIG.gui.background.forEach(this::setButton);
	}
}
