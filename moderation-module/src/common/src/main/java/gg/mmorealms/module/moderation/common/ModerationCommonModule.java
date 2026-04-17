package gg.mmorealms.module.moderation.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.moderation.ModerationModuleBuildConstants;

@Module(
		id = ModerationModuleBuildConstants.ID,
		version = ModerationModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = ModerationModuleBuildConstants.DEPENDENCIES
)
public abstract class ModerationCommonModule implements CommonModule {
}
