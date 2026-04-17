package gg.mmorealms.module.discord_chat.backend.neoforge;

import gg.mmorealms.module.discord_chat.DiscordChatModuleBuildConstants;
import gg.mmorealms.module.discord_chat.backend.common.DiscordChatBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(DiscordChatModuleBuildConstants.ID)
public class DiscordChatNeoForgeModule extends DiscordChatBackendModule {

	public DiscordChatNeoForgeModule() {
		this.setup();
	}

}
