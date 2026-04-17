package gg.mmorealms.module.essentials.velocity.command.whitelist.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.command.whitelist.WhitelistCommand;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"remove"}, parent = WhitelistCommand.class)
@Getter
@Setter
public class RemoveCommand extends VelocityCommand {

	public RemoveCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return List.of("player");
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String playerUsername = arguments.getFirst();

		EssentialsVelocityModule.instance().getConfig().whitelist.remove(playerUsername);
		sendMessage(sender, new MessageBuilder("Removed player {player} from the whitelist")
				.parse("player", playerUsername)
				.parse()
		);
	}


}
