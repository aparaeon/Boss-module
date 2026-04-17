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
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(aliases = {"delete"}, arguments = {"target", "currency"}, parent = BalanceCommand.class)
public class DeleteCommand extends BackendCommand {
	private @Inject EconomyConfig config;

	public DeleteCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
		if (argument.equals("target")) {
			return recommendPlayersList();
		}

		if (argument.equals("currency")) {
			return CurrencyType.getFriendlyNames();
		}

		return new ArrayList<>();
	}

	@Override
	protected final void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String username = arguments.get(0);
		String currencyID = arguments.get(1);

		IUser user = IUser.getByUsername(username);

		if (user == null) {
			sendMessage(sender, config.lang.invalidUser);
			return;
		}

		IBalances balance = IBalances.getByUUID(user.getUUID());

		CurrencyType currency = CurrencyType.parse(currencyID);

		if (currency == null) {
			sendMessage(sender, "Invalid currency");
			return;
		}

		balance.set(currency, 0.0, "ADMIN_COMMAND");

		sendMessage(sender, config.lang.successDeleteCurrency
				.parse("currency", currencyID)
				.parse("name", username)
				.parse()
		);
	}
}
