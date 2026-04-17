package gg.mmorealms.module.gyms.backend.fabric;

import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.fabric.dto.trainer.FabricTrainerPlatformImplementation;
import gg.mmorealms.module.gyms.backend.fabric.manager.GymBattlesManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class GymsFabricModule extends GymsBackendModule implements ModInitializer {
	@Getter
	@Accessors(fluent = true)
	private static GymsFabricModule instance;

	@Getter
	private GymBattlesManager gymBattlesManager;

	public GymsFabricModule() {
		GymsFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		gymBattlesManager = new GymBattlesManager();
	}

	@Override
	public FabricTrainerPlatformImplementation createTrainerImplementation() {
		return new FabricTrainerPlatformImplementation();
	}
}