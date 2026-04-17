package gg.mmorealms.module.chat.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;

public class StaffChatConfig {

	public MessageBuilder staffChatMessageFormat = new MessageBuilder(
		"<b><gold>[Staff Chat] </gold><yellow>{short-rank} {username}: </yellow><white>{message}</white>"
	);
	public String staffChatPrefix = "!";

}
