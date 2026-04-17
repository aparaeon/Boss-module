package gg.mmorealms.module.chat.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.chat.ChatModuleBuildConstants;

@Module(
		id = ChatModuleBuildConstants.ID,
		version = ChatModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = ChatModuleBuildConstants.DEPENDENCIES
)
public abstract class ChatCommonModule implements CommonModule {
}
