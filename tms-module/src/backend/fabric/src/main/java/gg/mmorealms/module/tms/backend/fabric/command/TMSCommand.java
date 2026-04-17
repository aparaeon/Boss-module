package gg.mmorealms.module.tms.backend.fabric.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.tms.backend.fabric.gui.PokemonPartyGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"tms"}, onlyFor = Command.OnlyFor.PLAYERS)
public class TMSCommand extends UserCommand {

	public TMSCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new PokemonPartyGUI(user).open();
	}
}
