package gg.mmorealms.module.tebex_integration.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.tebex_integration.backend.common.gui.PurchaseRewardsMainMenuGUI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"rewards", "store_rewards"}, onlyFor = Command.OnlyFor.PLAYERS)
public class RewardsCommand extends UserCommand {
	public RewardsCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		new PurchaseRewardsMainMenuGUI(user).open();
	}

}
