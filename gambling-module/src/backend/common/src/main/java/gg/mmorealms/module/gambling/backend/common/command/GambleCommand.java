package gg.mmorealms.module.gambling.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.gambling.backend.common.gui.GambleGUI;
import gg.mmorealms.module.gambling.backend.common.gui.RouletteGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"gamble", "roulette"})
public class GambleCommand extends UserCommand {
	public GambleCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new GambleGUI(user).open();
	}
}
