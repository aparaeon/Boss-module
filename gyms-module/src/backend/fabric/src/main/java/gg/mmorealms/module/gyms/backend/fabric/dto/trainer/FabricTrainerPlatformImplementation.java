package gg.mmorealms.module.gyms.backend.fabric.dto.trainer;

import gg.mmorealms.module.gyms.backend.common.dto.trainer.ITrainerPlatformImplementation;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.TrainerInfo;

public class FabricTrainerPlatformImplementation implements ITrainerPlatformImplementation {
	@Override
	public FabricTrainer spawn(TrainerInfo trainerInfo) {
		return new FabricTrainer(trainerInfo);
	}
}