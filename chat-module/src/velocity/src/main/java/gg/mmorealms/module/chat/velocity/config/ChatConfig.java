package gg.mmorealms.module.chat.velocity.config;

import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.util.List;

public class ChatConfig {

	public String chatColorPermission = "mmorealms.chat.colors";
	public List<String> blacklist = List.of(
			"fuck",
			"nigger"
	);

	public Range nicknameLength = new Range(3, 16);

	public Lang lang = new Lang();

	public static class Lang {
		public String cannotMessageSelf = "You cannot message yourself.";
		public String nooneToReplyTo = "<red>No one to reply to";
		public String playerNotFound = "<red>Player not found";
		public String blacklistedWord = "<red>This word is prohibited";
		public String nonAsciiCharacters = "<red>Your message contains non-ASCII characters";
		public MessageBuilder chatFormat = new MessageBuilder("<hover:show_text:'Click to send a private message to {user}'><click:suggest_command:'/msg {real_name}'>{prefix}{user}: {message}</click></hover>");
		public MessageBuilder msgFormat = new MessageBuilder("<gray>{from} <white>-> <gray>{to}: <yellow>{message}");
	}

}
