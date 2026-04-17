package gg.mmorealms.module.user_data.backend.neoforge;

import gg.mmorealms.module.user_data.UserDataModuleBuildConstants;
import gg.mmorealms.module.user_data.backend.common.UserDataBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(UserDataModuleBuildConstants.ID)
public class UserDataNeoForgeModule extends UserDataBackendModule {

	public UserDataNeoForgeModule() {
		this.setup();
	}

}
