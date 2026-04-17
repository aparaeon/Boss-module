package gg.mmorealms.module.gyms.backend.common.gui;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.config.GymsConfig;
import gg.mmorealms.module.gyms.backend.common.dto.database.GymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.dto.gym.GymRegion;

import java.util.List;

public class BadgesGUI extends GUI {
	private final static GymsConfig CONFIG = GymsBackendModule.instance().getConfig();
	private final static List<Integer> slots = CONFIG.badgesGUI.slots;

	public BadgesGUI(User user) {
		super(user, new Settings().chestSize(6));
	}

	@Override
	public void setup() {
		IUserGymRecord userGymRecord = IUserGymRecord.get(user);
		GymsBackendModule instance = GymsBackendModule.instance();

		int slotIndex = 0;

		for (GymRegion region : instance.getConfig().regions) {
			for (Gym gym : region.getGyms()) {
				GymRecord gymRecord = userGymRecord.getGymRecord(gym.getId());
				int slot = slots.get(slotIndex);
				if (gymRecord == null || gymRecord.wins() <= 0) {
					setButton(new GUIButton()
							.display(gym.getInactiveBadgeItem())
							.displayName(gym.getName())
							.position(slot));
					return;
				}

				setButton(new GUIButton()
						.display(gym.getActiveBadgeItem())
						.displayName(gym.getName())
						.position(slot));

				slotIndex++;
				if (slotIndex >= slots.size()) {
					Logger.error("Insufficient slots in the BadgesGUI");
					return;
				}
			}
		}
	}

	@Override
	public String getTitleString() {
		return CONFIG.badgesGUI.title;
	}
}