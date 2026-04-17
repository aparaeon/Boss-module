package gg.mmorealms.module.economy.backend.common.command.sub;

import com.mojang.brigadier.context.CommandContext;
import com.raduvoinea.commandmanager.backend.common.command.BackendCommand;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.economy.backend.common.config.EconomyConfig;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import lombok.Setter;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Setter
public class ModifyBalanceCommand extends BackendCommand {

	private @Inject EconomyConfig config;
	private @Inject MinecraftServer server;

	private boolean remove = false;

	public ModifyBalanceCommand(CommonCommandManager commandManager, boolean remove) {
		super(commandManager);
		this.remove = remove;
	}

	public ModifyBalanceCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> getArguments() {
		return List.of("target", "amount", "currency");
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
	protected synchronized final void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {
		String username = arguments.get(0);
		String amountString = arguments.get(1);
		String currencyString = arguments.get(2);

		double amount;
		try {
			amount = Double.parseDouble(amountString);
		} catch (Exception e) {
			sendMessage(sender, config.lang.invalidAmount);
			return;
		}

		if (remove) {
			amount = -amount;
		}

		CurrencyType currencyType = CurrencyType.parse(currencyString);

		if (username.equals("__all__")) {
			for (String playerName : server.getPlayerNames()) {
				IUser user = IUser.getByUsername(playerName);
				if (user == null) {
					Logger.warn("User " + playerName + " not found, skipping balance modification.");
					continue;
				}

				execute(sender, user, amount, currencyType);
			}
			return;
		}

		IUser user = IUser.getByUsername(username);

		if (user == null) {
			sendMessage(sender, config.lang.invalidUser);
			return;
		}

		execute(sender, user, amount, currencyType);
	}

	private void execute(CommandSource sender, IUser user, double amount, CurrencyType currency) {
		IBalances balance = IBalances.getByUUID(user.getUUID());

		balance.add(currency, amount, "ADMIN_COMMAND");

		MessageBuilder message = config.lang.successAddAmount;
		if (remove) {
			message = config.lang.successRemoveAmount;
			amount = -amount;
		}

		sendMessage(sender, message
				.parse("name", user.getUsername())
				.parse("amount", amount)
				.parse("currency", currency.getName())
		);
	}
}
