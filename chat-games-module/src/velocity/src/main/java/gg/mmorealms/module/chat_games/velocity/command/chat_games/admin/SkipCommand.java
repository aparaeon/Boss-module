package gg.mmorealms.module.chat_games.velocity.command.chat_games.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;

import java.util.List;

@Command(aliases = {"skip"}, parent = ChatGamesAdminCommand.class)
public class SkipCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public SkipCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		boolean hadActive = ChatGamesVelocityModule.instance().getChatGamesManager().skip();
		String msg = hadActive
			? ChatGamesVelocityModule.instance().getConfig().lang.adminSkipped.parse()
			: ChatGamesVelocityModule.instance().getConfig().lang.adminNothingToSkip.parse();
		sender.sendMessage(miniMessageManager.parse(msg));
	}

}
