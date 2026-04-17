package gg.mmorealms.module.discord_link.backend.common;

import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.discord_link.common.DiscordLinkCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public class DiscordLinkBackendModule extends DiscordLinkCommonModule implements BackendModule {

	// Static
	@Accessors(fluent = true)
	@Getter
	private static DiscordLinkBackendModule instance;

	public DiscordLinkBackendModule() {
		DiscordLinkBackendModule.instance = this;
	}

	@Override
	public void onInit() {

	}

	@Override
	public void onEnable() {

	}
}