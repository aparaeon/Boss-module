package gg.mmorealms.module.essentials.velocity.command.whitelist.sub;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.essentials.velocity.EssentialsVelocityModule;
import gg.mmorealms.module.essentials.velocity.command.whitelist.WhitelistCommand;
import gg.mmorealms.module.essentials.velocity.manager.WhitelistManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"add"}, parent = WhitelistCommand.class)
@Getter
@Setter
public class AddCommand extends VelocityCommand {

	private @Inject WhitelistManager whitelistManager;

	public AddCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		return List.of("player");
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String playerUsername = arguments.getFirst();

		whitelistManager.setBypass(playerUsername, true);

		sendMessage(sender, new MessageBuilder("Added player {player} to the whitelist")
				.parse("player", playerUsername)
				.parse()
		);
	}


}
