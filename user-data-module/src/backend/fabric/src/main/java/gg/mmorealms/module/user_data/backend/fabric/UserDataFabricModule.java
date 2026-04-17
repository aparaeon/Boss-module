package gg.mmorealms.module.user_data.backend.fabric;

import gg.mmorealms.module.user_data.backend.common.UserDataBackendModule;
import net.fabricmc.api.ModInitializer;

public class UserDataFabricModule extends UserDataBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

}
