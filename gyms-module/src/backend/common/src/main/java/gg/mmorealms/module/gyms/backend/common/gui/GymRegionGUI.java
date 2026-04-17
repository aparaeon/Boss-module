package gg.mmorealms.module.gyms.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.CooldownInfo;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.dto.gym.GymRegion;

import java.util.List;

public class GymRegionGUI extends GUI {
	private final static GymsConfig CONFIG = GymsBackendModule.instance().getConfig();
	private final static List<Integer> slots = CONFIG.gymRegionGUI.slots;
	private final GymRegion gymRegion;

	public GymRegionGUI(User user, GymRegion gymRegion) {
		super(user, new Settings()
				.chestSize(5));

		this.gymRegion = gymRegion;
	}

	@Override
	public void setup() {
		CONFIG.gymRegionGUI.background.forEach(this::setButton);

		for (int i = 0; i < slots.size() && i < gymRegion.getGyms().size(); i++) {
			int slot = slots.get(i);
			Gym gym = gymRegion.getGyms().get(i);

			if (!gym.requiredGymsCheck(user.getUUID())) {
				break;
			}

			CooldownInfo activeCooldownInfo = gym.getActiveCooldownInfo(getUser().getUUID());
			String cooldownMessage = CONFIG.lang.canBattleGymDescription;
			if (activeCooldownInfo != null) {
				cooldownMessage = CONFIG.lang.gymOnCooldownDescription
						.parse("cooldown", activeCooldownInfo.getFormattedTime())
						.parse();
			}

			setButton(new GUIButton()
					.display(gym.getDisplayItem(), true)
					.displayName(gym.getName())
					.lore(gym.getDescription()
							.parse("cooldown_message", cooldownMessage)
					)
					.position(slot))
					.onClick(event -> {
						gym.teleportUser(user);
						this.close();
					});
		}
	}

	@Override
	public String getTitleString() {
		return gymRegion.getName();
	}
}