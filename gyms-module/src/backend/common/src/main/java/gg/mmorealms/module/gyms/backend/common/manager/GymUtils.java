package gg.mmorealms.module.gyms.backend.common.manager;

import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.dto.gym.GymRegion;

public class GymUtils {

	// NPCEntity for cobblemon
	public static Gym getGymByNPC(Object trainer) {
		for (GymRegion region : GymsBackendModule.instance().getConfig().regions) {
			for (Gym gym : region.getGyms()) {
				if (gym.getTrainer() == null) {
					continue;
				}

				if (gym.getTrainer().equals(trainer)) {
					return gym;
				}
			}
		}

		return null;
	}
}