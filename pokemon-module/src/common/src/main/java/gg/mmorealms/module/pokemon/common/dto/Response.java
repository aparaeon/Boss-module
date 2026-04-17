package gg.mmorealms.module.pokemon.common.dto;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response {
	private boolean accepted;
	private String message;

	public Response(boolean accepted, MessageBuilder message) {
		this.accepted = accepted;
		this.message = message.parse();
	}
}
