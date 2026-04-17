package gg.mmorealms.module.chat.velocity.command.msg;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.chat.velocity.ChatVelocityModule;
import gg.mmorealms.module.chat.velocity.config.ChatConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Command(aliases = {"msg", "private_message"}, onlyFor = Command.OnlyFor.PLAYERS)
public class PrivateMessageCommand extends VelocityCommand {

	private @Inject ChatConfig config;

	public PrivateMessageCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}


	@Override
	protected void executePlayer(Player player, List<String> arguments) {
		String targetName = arguments.getFirst();
		String message = String.join(" ", arguments.subList(1, arguments.size()));

		if (targetName.equals(player.getUsername())) {
			sendMessage(player, config.lang.cannotMessageSelf);
			return;
		}

		Optional<Player> targetOptional = ChatVelocityModule.instance().getProxy().getPlayer(targetName);

		if (targetOptional.isEmpty()) {
			return;
		}

		Player target = targetOptional.get();

		ChatVelocityModule.instance().getMessageManager().sendPrivateMessage(player, target, message);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		if (arguments.size() == 1) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

}
