package gg.mmorealms.module.chat_games.velocity.command.chat_games.admin;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.chat_games.velocity.ChatGamesVelocityModule;

import java.util.List;

@Command(aliases = {"force"}, parent = ChatGamesAdminCommand.class)
public class ForceCommand extends VelocityCommand {

	private @Inject VelocityMiniMessageManager miniMessageManager;

	public ForceCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		ChatGamesVelocityModule.instance().getChatGamesManager().force();
		sender.sendMessage(miniMessageManager.parse(
			ChatGamesVelocityModule.instance().getConfig().lang.adminForced.parse()
		));
	}

}
