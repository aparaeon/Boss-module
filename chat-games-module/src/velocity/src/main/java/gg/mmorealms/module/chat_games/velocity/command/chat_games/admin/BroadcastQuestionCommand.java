package gg.mmorealms.module.chat_games.velocity.command.chat_games.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(aliases = {"broadcastquestion", "bq"}, parent = ChatGamesAdminCommand.class)
public class BroadcastQuestionCommand extends VelocityCommand {

	// Matches two double-quoted strings separated by whitespace.
	// Example: "What does pikachu evolve to?" "raichu"
	// Group 1 = question text, Group 2 = answer text
	private static final Pattern ARGS_PATTERN = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)+)\"\\s+\"((?:[^\"\\\\]|\\\\.)+)\"");

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public BroadcastQuestionCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		Matcher matcher = ARGS_PATTERN.matcher(String.join(" ", arguments));
		if (!matcher.find()) {
			sender.sendMessage(miniMessageManager.parse(getUsage()));
			return;
		}

		String question = matcher.group(1).replace("\\\"", "\"");
		String answer = matcher.group(2).replace("\\\"", "\"");

		boolean started = ChatGamesVelocityModule.instance().getChatGamesManager()
			.broadcastQuestion(question, answer);

		String msg = started
			? ChatGamesVelocityModule.instance().getConfig().lang.adminBroadcasted.parse()
			: ChatGamesVelocityModule.instance().getConfig().lang.adminQuestionAlreadyActive.parse();
		sender.sendMessage(miniMessageManager.parse(msg));
	}

}
