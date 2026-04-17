package gg.mmorealms.module.chat.velocity.manager;

import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.config.ChatConfig;
import gg.mmorealms.module.chat.velocity.dto.LocalChatRequest;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MessageManager {

	private final HashMap<String, String> replyMap = new HashMap<>();
	private final ChatConfig config = ChatVelocityModule.instance().getConfig();

	public void sendReply(@NotNull Player from, @NotNull String message) {
		String toUsername = replyMap.getOrDefault(from.getUsername(), null);

		if (toUsername == null) {
			from.sendMessage(ChatVelocityModule.instance().getMiniMessageManager().parse(config.lang.nooneToReplyTo));
			return;
		}

		Optional<Player> toOptional = ChatVelocityModule.instance().getProxy().getPlayer(toUsername);

		if (toOptional.isEmpty()) {
			from.sendMessage(ChatVelocityModule.instance().getMiniMessageManager().parse(config.lang.playerNotFound));
			return;
		}

		Player to = toOptional.get();

		sendPrivateMessage(from, to, message);
	}

	public void sendPrivateMessage(@NotNull Player from, @NotNull Player to, @NotNull String message) {
		LocalChatRequest chatRequest = new LocalChatRequest(from, message, true, List.of(
				from.getUniqueId(),
				to.getUniqueId()
		));
		LocalChatRequest.Response result = chatRequest.fireSync();

		if (result != null && !result.isAllowed()) {
			from.sendMessage(ChatVelocityModule.instance().getMiniMessageManager().parse(result.getErrorMessage()));
			return;
		}

		message = ChatVelocityModule.instance().getMiniMessageManager().sanitize(message);

		message = config.lang.msgFormat
				.parse("from", from.getUsername())
				.parse("to", to.getUsername())
				.parse("message", message)
				.parse();

		replyMap.put(to.getUsername(), from.getUsername());
		replyMap.put(from.getUsername(), to.getUsername());
		to.sendMessage(ChatVelocityModule.instance().getMiniMessageManager().parse(message));
		from.sendMessage(ChatVelocityModule.instance().getMiniMessageManager().parse(message));

		Logger.info(ChatVelocityModule.instance().getMiniMessageManager().sanitize(message));
	}

	public void sendGlobalMessage(String message) {
		Component messageComponent = ChatVelocityModule.instance().getMiniMessageManager().toNative(
				ChatVelocityModule.instance().getMiniMessageManager().toComponent(message)
		);
		ChatVelocityModule.instance().getProxy().sendMessage(messageComponent);
	}

}
