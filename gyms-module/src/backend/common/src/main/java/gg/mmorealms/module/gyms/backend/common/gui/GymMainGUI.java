package gg.mmorealms.module.gyms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.gym.GymRegion;

import java.util.List;

public class GymMainGUI extends GUI {
	private final static GymsConfig CONFIG = GymsBackendModule.instance().getConfig();
	private final static List<Integer> slots = CONFIG.gymMainGUI.slots;


	public GymMainGUI(User user) {
		super(user, new Settings()
				.chestSize(3));
	}

	@Override
	public void setup() {
		CONFIG.gymMainGUI.background.forEach(this::setButton);

		for (int i = 0; i < slots.size() && i < CONFIG.regions.size(); i++) {
			int slot = slots.get(i);
			GymRegion region = CONFIG.regions.get(i);

			if (!region.requiredGymsCheck(user.getUUID())) {
				break;
			}

			setButton(new GUIButton()
					.display(region.getDisplayItem())
					.displayName(region.getName())
					.position(slot))
					.onClick(event -> new GymRegionGUI(user, region).open());
		}
	}

	@Override
	public String getTitleString() {
		return CONFIG.gymMainGUI.title;
	}
}