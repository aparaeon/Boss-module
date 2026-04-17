package gg.mmorealms.module.discord_link.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.discord_link.DiscordLinkModuleBuildConstants;

@Module(
		id = DiscordLinkModuleBuildConstants.ID,
		version = DiscordLinkModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = DiscordLinkModuleBuildConstants.DEPENDENCIES
)
public abstract class DiscordLinkCommonModule implements CommonModule {
}
