package gg.mmorealms.module.gyms.backend.fabric.dto.trainer;

import com.cobblemon.mod.common.api.npc.NPCClass;
import com.cobblemon.mod.common.api.npc.NPCClasses;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.ITrainer;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.TrainerInfo;
import gg.mmorealms.module.gyms.backend.fabric.GymsFabricModule;
import jakarta.validation.constraints.NotNull;

public class FabricTrainer implements ITrainer {
	public NPCEntity nativeTrainer;

	public FabricTrainer(TrainerInfo trainerInfo) {
		if (trainerInfo == null) {
			Logger.error("Empty NPC Trainer info");
			return;
		}

		Location location = trainerInfo.getLocation();
		String trainerID = trainerInfo.getId();

		if (trainerID == null) {
			Logger.error("Null trainer ID");
			return;
		}

		NPCClass npcClass = NPCClasses.INSTANCE.getByName(trainerID);
		if (npcClass == null) {
			Logger.error("Invalid NPC Trainer ID: " + trainerID);
			return;
		}

		nativeTrainer = new NPCEntity(GymsFabricModule.instance().getServer().overworld());
		nativeTrainer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		nativeTrainer.setNpc(npcClass);
		nativeTrainer.initialize(trainerInfo.getLevel());
		GymsFabricModule.instance().getServer().overworld().addFreshEntity(nativeTrainer);
	}

	@Override
	public Object getNative() {
		return nativeTrainer;
	}

	@Override
	public boolean equals(@NotNull Object trainer) {
		if (trainer instanceof NPCEntity npcTrainer &&
				npcTrainer.getNpc().getId().equals(nativeTrainer.getNpc().getId())) {
			return true;
		}
		return false;
	}

}