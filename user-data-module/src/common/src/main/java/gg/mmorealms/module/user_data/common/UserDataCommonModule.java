package gg.mmorealms.module.user_data.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.user_data.UserDataModuleBuildConstants;

@Module(
		id = UserDataModuleBuildConstants.ID,
		version = UserDataModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = UserDataModuleBuildConstants.DEPENDENCIES
)
public abstract class UserDataCommonModule implements CommonModule {
}
