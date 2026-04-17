package gg.mmorealms.module.chat.velocity.dto;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.common.dto.event.local.LocalRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
// TODO Maybe rename to something like ChatEvent or PlayerChatEvent
public class LocalChatRequest extends LocalRequest<LocalChatRequest.Response> {

	private final Player player; // TODO Rename to sender
	private @Setter String message;
	private final boolean isPrivate;
	private final List<UUID> recipients;

	public LocalChatRequest(Player player, String message, boolean isPrivate, List<UUID> recipients) {
		super(Response.allow());
		this.player = player;
		this.message = message;
		this.isPrivate = isPrivate;
		this.recipients = new ArrayList<>(recipients);
	}

	@Override
	public boolean isCancelled() {
		return !this.getResult().allowed;
	}

	@Getter
	@AllArgsConstructor
	public static class Response {
		private boolean allowed;
		private String errorMessage;

		public static Response allow() {
			return new Response(true, null);
		}

		public static Response deny(String errorMessage) {
			return new Response(false, errorMessage);
		}
	}

}
