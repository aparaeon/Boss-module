package gg.mmorealms.module.discord_link.backend.neoforge;

import gg.mmorealms.module.discord_link.DiscordLinkModuleBuildConstants;
import gg.mmorealms.module.discord_link.backend.common.DiscordLinkBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(DiscordLinkModuleBuildConstants.ID)
public class DiscordLinkNeoForgeModule extends DiscordLinkBackendModule {

	public DiscordLinkNeoForgeModule() {
		this.setup();
	}

}
