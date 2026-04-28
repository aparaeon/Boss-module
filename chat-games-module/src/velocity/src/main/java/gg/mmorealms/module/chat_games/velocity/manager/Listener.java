package gg.mmorealms.module.chat_games.velocity.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;

public class Listener {

	private @Inject ChatGamesManager chatGamesManager;

	@EventHandler(order = 100_000)
	private void onLocalChatRequest(LocalChatRequest event) {
		if (!event.getResult().isAllowed()) {
			return;
		}

		boolean answered = chatGamesManager.tryAnswer(
			event.getPlayer().getUniqueId(),
			event.getPlayer().getUsername(),
			event.getMessage()
		);

		if (answered) {
			event.setResult(LocalChatRequest.Response.deny(null));
		}
	}

}
