package gg.mmorealms.module.chat.backend.neoforge;

import gg.mmorealms.module.chat.ChatModuleBuildConstants;
import gg.mmorealms.module.chat.backend.common.ChatBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(ChatModuleBuildConstants.ID)
public class ChatNeoForgeModule extends ChatBackendModule {

	public ChatNeoForgeModule() {
		this.setup();
	}

}
