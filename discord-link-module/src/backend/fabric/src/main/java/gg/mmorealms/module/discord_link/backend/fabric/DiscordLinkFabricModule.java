package gg.mmorealms.module.discord_link.backend.fabric;

import gg.mmorealms.module.discord_link.backend.common.DiscordLinkBackendModule;
import net.fabricmc.api.ModInitializer;

public class DiscordLinkFabricModule extends DiscordLinkBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

}
