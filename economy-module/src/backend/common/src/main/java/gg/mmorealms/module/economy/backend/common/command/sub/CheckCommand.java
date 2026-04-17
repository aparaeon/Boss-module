package gg.mmorealms.module.economy.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.economy.backend.common.command.BalanceCommand;
import gg.mmorealms.module.economy.backend.common.config.EconomyConfig;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"check"}, arguments = {"target"}, parent = BalanceCommand.class)
public class CheckCommand extends BackendCommand {
	private @Inject EconomyConfig config;

	public CheckCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("target")) {
			return recommendPlayersList();
		}

		return new ArrayList<>();
	}

	@Override
	protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String username = arguments.getFirst();
		IUser user = IUser.getByUsername(username);

		if (user == null) {
			sendMessage(sender, config.lang.invalidUser);
			return;
		}

		IBalances balances = IBalances.getByUUID(user.getUUID());

		sendMessage(sender, config.lang.headerCheckBalance
				.parse("user", username)
				.parse("balances", balances.toPrettyString(false, true))
		);
	}
}
