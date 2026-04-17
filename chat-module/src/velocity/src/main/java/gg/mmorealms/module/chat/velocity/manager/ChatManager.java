package gg.mmorealms.module.chat.velocity.manager;

import com.raduvoinea.commandmanager.common.utils.LuckPermsUtils;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.config.ChatConfig;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;
import gg.mmorealms.module.chat.velocity.dto.user_settings.NicknameSetting;
import gg.mmorealms.module.user_data.velocity.database.VelocityUserSettings;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.util.List;

public class ChatManager {

	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ChatConfig config;

	@Setter
	@Getter
	private boolean muted = false;

	public void handleChatMessage(Player player, String message) {
		LocalChatRequest chatRequest = new LocalChatRequest(player, message, false, ChatVelocityModule.instance().getProxy().getAllPlayers().stream().map(Player::getUniqueId).toList());
		LocalChatRequest.Response result = chatRequest.fireSync();

		if (result != null && !result.isAllowed()) {
			if (result.getErrorMessage() != null) {
				player.sendMessage(miniMessageManager.parse(result.getErrorMessage()));
			}
			return;
		}

		Component parsedMessage = convert(player, chatRequest.getMessage());
		for (Player targetPlayer : ChatVelocityModule.instance().getProxy().getAllPlayers()) {
			if (!chatRequest.getRecipients().contains(targetPlayer.getUniqueId())) {
				continue;
			}

			targetPlayer.sendMessage(parsedMessage);
		}

		Logger.log(new MessageBuilder("[Chat] {user}: {message}")
				.parse("user", player.getUsername())
				.parse("message", chatRequest.getMessage())
				.toString());
	}

	private Component convert(Player player, String message) {
		if (!player.hasPermission(config.chatColorPermission)) {
			message = miniMessageManager.sanitize(message);
		}

		String nickname = VelocityUserSettings.getByUUID(player.getUniqueId()).get(NicknameSetting.class).getValue();

		return miniMessageManager.toNative(
				miniMessageManager.toComponent(config.lang.chatFormat
						.parse("prefix", LuckPermsUtils.getPrefix(player.getUniqueId()))
						.parse("user", nickname.isEmpty() ? player.getUsername() : nickname)
						.parse("real_name", player.getUsername())
						.parse("message", message)
				)
		);
	}

}
