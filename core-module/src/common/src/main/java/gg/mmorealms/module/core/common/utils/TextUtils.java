package gg.mmorealms.module.core.common.utils;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import org.jetbrains.annotations.Nullable;

public class TextUtils {

	public static MessageBuilder createClickableText(String text, @Nullable String hoverMessage, @Nullable String clickCommand, boolean executeCommand) {
		String commandVerb = executeCommand ? "run_command" : "suggest_command";

		String hoverStart = hoverMessage == null ? "" : "<hover:show_text:'{hoverMessage}'>";
		String hoverEnd = hoverMessage == null ? "" : "</hover>";

		String clickStart = clickCommand == null ? "" : "<click:{commandVerb}:'{clickCommand}'>";
		String clickEnd = clickCommand == null ? "" : "</click>";

		return new MessageBuilder("{hover_start}{click_start}{text}{click_end}{hover_end}")
				.parse("hover_start", hoverStart)
				.parse("hover_end", hoverEnd)
				.parse("click_start", clickStart)
				.parse("click_end", clickEnd)
				.parse("hoverMessage", hoverMessage)
				.parse("hoverMessage", hoverMessage)
				.parse("hoverMessage", hoverMessage)

				.parse("hoverMessage", hoverMessage)
				.parse("text", text)
				.parse("clickCommand", clickCommand)
				.parse("commandVerb", commandVerb);
	}

}
