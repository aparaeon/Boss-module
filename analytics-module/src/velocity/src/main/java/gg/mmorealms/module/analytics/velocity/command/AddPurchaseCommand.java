package gg.mmorealms.module.analytics.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(aliases = {"add_purchase", "add_revenue"}, arguments = {"target", "revenue"})
public class AddPurchaseCommand extends VelocityCommand {
	public AddPurchaseCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	public @NotNull List<String> onAutoComplete(List<String> arguments) {
		if (arguments.size() == 1) {
			return recommendPlayersList();
		}
		return List.of();
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		String targetUsernameOrUUID = arguments.getFirst();
		String amountString = arguments.get(1);

		UserStats stats = AnalyticsVelocityModule.instance().getByUUIDOrUsername(
				targetUsernameOrUUID,
				UserStats::getByPlayer,
				UserStats::getByUUID,
				(ignored) -> null
		);

		if (stats == null) {
			sendMessage(sender, "User not found"); // TODO Config
			return;
		}

		double amount;
		try {
			amount = Double.parseDouble(amountString);
		} catch (NumberFormatException e) {
			sendMessage(sender, "Invalid amount");
			return;
		}

		stats.recordPurchase(amount);
		sendMessage(sender, "Added revenue");
	}
}
