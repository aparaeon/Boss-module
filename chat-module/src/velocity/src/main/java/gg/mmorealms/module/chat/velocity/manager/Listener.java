package gg.mmorealms.module.chat.velocity.manager;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.common.dto.GlobalMessageEvent;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.config.ChatConfig;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;
import gg.mmorealms.module.core.common.dto.chat.CreateChatCaptureEvent;

import java.util.List;

public class Listener {

	private @Inject VelocityChatInputManager chatInputManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ChatManager chatManager;
	private @Inject ChatConfig config;

	@EventHandler
	private void onPlayerChatEvent(PlayerChatEvent event) {
		//noinspection deprecation
		event.setResult(PlayerChatEvent.ChatResult.denied());

		this.chatManager.handleChatMessage(event.getPlayer(), event.getMessage());
	}

	@EventHandler
	public void onGlobalMessageEvent(GlobalMessageEvent event) {
		ChatVelocityModule.instance().getMessageManager().sendGlobalMessage(event.getMessage());
	}

	@EventHandler
	public void onCreateChatCaptureEvent(CreateChatCaptureEvent event) {
		chatInputManager.registerInputCallback(event.getUuid());
	}

	@EventHandler(order = -100_000)
	public void handleChatInput(LocalChatRequest event) {
		boolean result = chatInputManager.provideInput(event.getPlayer(), event.getMessage());

		if (result) {
			event.setResult(LocalChatRequest.Response.deny(null));
		}
	}

	@EventHandler
	public void handleMutedChat(LocalChatRequest event) {
		Player player = event.getPlayer();

		if (chatManager.isMuted()) {
			if (!LuckPermsUtils.checkPermission(Player.class, player, ChatVelocityModule.CHAT_MUTE_OVERRIDE_PERMISSION)) {
				event.setResult(LocalChatRequest.Response.deny("Chat has been muted by an administrator."));
			}
		}
	}

	@EventHandler
	public void handleChatFiltration(LocalChatRequest event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		for (char c : message.toCharArray()) {
			if (c < 32 || c > 126) {
				event.setResult(LocalChatRequest.Response.deny(config.lang.nonAsciiCharacters));
			}
		}

		List<String> blacklistedWords = ChatVelocityModule.instance().getConfig().blacklist;
		for (String word : message.split(" ")) {
			if (blacklistedWords.contains(word.toLowerCase())) {
				player.sendMessage(miniMessageManager.parse(ChatVelocityModule.instance().getConfig().lang.blacklistedWord));
				return;
			}
		}

	}

}
