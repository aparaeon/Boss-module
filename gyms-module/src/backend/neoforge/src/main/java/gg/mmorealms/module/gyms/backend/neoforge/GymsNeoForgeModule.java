package gg.mmorealms.module.gyms.backend.neoforge;

import gg.mmorealms.module.gyms.GymsModuleBuildConstants;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.ITrainerPlatformImplementation;
import net.neoforged.fml.common.Mod;

@Mod(GymsModuleBuildConstants.ID)
public class GymsNeoForgeModule extends GymsBackendModule {

	public GymsNeoForgeModule() {
		this.setup();
	}

	@Override
	// TODO
	public ITrainerPlatformImplementation createTrainerImplementation() {
		return null;
	}

}