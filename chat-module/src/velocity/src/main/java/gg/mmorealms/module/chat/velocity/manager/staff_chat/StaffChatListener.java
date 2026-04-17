package gg.mmorealms.module.chat.velocity.manager.staff_chat;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.config.StaffChatConfig;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;

public class StaffChatListener {

	private @Inject StaffChatManager staffChatManager;
	private @Inject StaffChatConfig staffChatConfig;

	@EventHandler
	public void onLoginEvent(LoginEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission(ChatVelocityModule.STAFF_CHAT_PERMISSION)) {
			staffChatManager.onStaffJoin(player);
		}
	}

	@EventHandler
	public void onDisconnectEvent(DisconnectEvent event) {
		Player player = event.getPlayer();

		if (player.hasPermission(ChatVelocityModule.STAFF_CHAT_PERMISSION)) {
			staffChatManager.onStaffLeave(player);
		}
	}

	@EventHandler(order = -1_000, skipCancelled = false)
	public void onLocalChatRequest(LocalChatRequest chatRequest) {
		if (!chatRequest.getPlayer().hasPermission(ChatVelocityModule.STAFF_CHAT_PERMISSION)) {
			return;
		}

		if (chatRequest.isPrivate()) {
			return;
		}

		boolean isStaffChat;

		if (chatRequest.getMessage().startsWith(this.staffChatConfig.staffChatPrefix)) {
			isStaffChat = true;
			chatRequest.setMessage(chatRequest.getMessage().substring(this.staffChatConfig.staffChatPrefix.length())); // Remove the prefix
		} else {
			isStaffChat = this.staffChatManager.isInStaffChat(chatRequest.getPlayer());
		}

		if (!isStaffChat) {
			return;
		}

		chatRequest.setResult(LocalChatRequest.Response.deny(null)); // We do not need to display an error message as the message will be sent in the staff chat
		this.staffChatManager.sendStaffChatMessage(chatRequest.getPlayer(), chatRequest.getMessage());
	}

}
