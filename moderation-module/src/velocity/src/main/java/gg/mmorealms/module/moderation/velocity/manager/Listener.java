package gg.mmorealms.module.moderation.velocity.manager;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;
import gg.mmorealms.module.moderation.velocity.database.UserBan;
import gg.mmorealms.module.moderation.velocity.database.UserMute;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;

public class Listener {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	@EventHandler(order = -100_000)
	public void onPlayerChatEvent(LocalChatRequest event) {
		UserPunishments userPunishments = UserPunishments.getByUUID(event.getPlayer().getUniqueId());

		if (!userPunishments.isMuted()) {
			return;
		}

		UserMute mute = userPunishments.getMute();

		if (mute == null) {
			return;
		}

		event.setResult(LocalChatRequest.Response.deny(mute.toString()));
	}


	@EventHandler(order = 100_000)
	public void onPlayerChooseInitialServerEvent(PlayerChooseInitialServerEvent event) {
		UserPunishments userPunishments = UserPunishments.getByPlayer(event.getPlayer());

		if (!userPunishments.isBanned()) {
			return;
		}

		UserBan ban = userPunishments.getBan();

		if (ban == null) {
			return;
		}

		event.getPlayer().disconnect(miniMessageManager.parse(ban.toString()));
	}

}
