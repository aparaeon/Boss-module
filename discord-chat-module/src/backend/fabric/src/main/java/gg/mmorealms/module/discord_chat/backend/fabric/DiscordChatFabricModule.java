package gg.mmorealms.module.discord_chat.backend.fabric;

import gg.mmorealms.module.discord_chat.backend.common.DiscordChatBackendModule;
import net.fabricmc.api.ModInitializer;

public class DiscordChatFabricModule extends DiscordChatBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

}
