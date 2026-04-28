package gg.mmorealms.module.chat_games.velocity.command.chat_games.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.chat_games.velocity.command.chat_games.ChatGamesCommand;

import java.util.List;

@Command(aliases = {"admin"}, parent = ChatGamesCommand.class)
public class ChatGamesAdminCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public ChatGamesAdminCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		sender.sendMessage(miniMessageManager.parse(getUsage()));
	}

}
