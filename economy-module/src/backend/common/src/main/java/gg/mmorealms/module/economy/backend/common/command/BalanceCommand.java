package gg.mmorealms.module.economy.backend.common.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.module.core.backend.common.command.UserCommand;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"bal", "balance"}, onlyFor = Command.OnlyFor.PLAYERS)
public class BalanceCommand extends UserCommand {
	public BalanceCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	protected void executeUser(@NotNull User user, @NotNull List<String> arguments) {
		IBalances balances = IBalances.getByUser(user);

		user.sendMessage(balances.toPrettyString(true, false));
	}

}
