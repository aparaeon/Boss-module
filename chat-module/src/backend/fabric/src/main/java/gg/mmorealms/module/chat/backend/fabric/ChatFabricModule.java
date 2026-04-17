package gg.mmorealms.module.chat.backend.fabric;

import gg.mmorealms.module.chat.backend.common.ChatBackendModule;
import net.fabricmc.api.ModInitializer;

public class ChatFabricModule extends ChatBackendModule implements ModInitializer {

	@Override
	public void onInitialize() {
		this.setup();
	}

}
