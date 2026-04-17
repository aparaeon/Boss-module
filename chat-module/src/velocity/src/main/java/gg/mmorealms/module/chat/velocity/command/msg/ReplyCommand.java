package gg.mmorealms.module.chat.velocity.command.msg;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;

import java.util.List;

@Command(aliases = {"reply", "r"}, onlyFor = Command.OnlyFor.PLAYERS)
public class ReplyCommand extends VelocityCommand {

	public ReplyCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String message = String.join(" ", arguments);

		ChatVelocityModule.instance().getMessageManager().sendReply(player, message);
	}
}
