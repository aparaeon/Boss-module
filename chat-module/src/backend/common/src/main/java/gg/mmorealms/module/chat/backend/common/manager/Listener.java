package gg.mmorealms.module.chat.backend.common.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerLeaveEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.GameMessageEvent;
import gg.mmorealms.module.chat.backend.common.ChatBackendModule;
import gg.mmorealms.module.core.common.dto.chat.ChatCaptureResultEvent;

public class Listener {

	private @Inject BackendChatInputManager chatInputManager;

	@EventHandler
	private void onGameMessage(GameMessageEvent event) {
		for (String blockedGameMessage : ChatBackendModule.instance().getConfig().blockedGameMessages) {
			if (event.getMessage().getString().toLowerCase().contains(blockedGameMessage.toLowerCase())) {
				event.setResult(false);
				return;
			}
		}
	}

	@EventHandler
	private void onChatCaptureResultEvent(ChatCaptureResultEvent event) {
		chatInputManager.provideInput(event.getUuid(), event.getInput());
	}

	@EventHandler
	private void onChatCaptureResultEvent(PlayerLeaveEvent event) {
		chatInputManager.unregisterInputCallback(event.getPlayer().getUUID());
	}

}
