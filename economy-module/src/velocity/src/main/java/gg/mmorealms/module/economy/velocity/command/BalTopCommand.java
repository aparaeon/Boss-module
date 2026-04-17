package gg.mmorealms.module.economy.velocity.command;

import com.raduvoinea.commandmanager.common.annotation.Command;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.velocity.command.VelocityCommand;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.velocitypowered.api.command.CommandSource;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.velocity.EconomyVelocityModule;
import gg.mmorealms.module.economy.velocity.config.EconomyConfig;
import gg.mmorealms.module.economy.velocity.dto.TopResult;
import gg.mmorealms.module.economy.velocity.manager.TopManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Command(aliases = {"baltop"})
@Getter
@Setter
public class BalTopCommand extends VelocityCommand {
	private @Inject EconomyConfig config;

	private boolean specific = false;

	public BalTopCommand(CommonCommandManager commandManager) {
		super(commandManager);
	}

	@Override
	protected void executeCommon(CommandSource sender, List<String> arguments) {
		sendMessage(sender, config.lang.headerBalTop);

		if (!specific) {
			executeAll(sender);
			return;
		}

		String currency = arguments.getFirst();

		List<TopResult> topPlayers = EconomyVelocityModule.instance().getTopManager().getTopPlayers(currency);
		if (topPlayers == null || topPlayers.isEmpty()) {
			sendMessage(sender, config.lang.notFoundBalance);
			return;
		}
		sendMessage(sender, showEntries(topPlayers).toString());
	}


	protected final void executeAll(CommandSource sender) {
		Map<String, List<TopResult>> topPlayers = TopManager.getTopPlayers();

		StringBuilder stringBuilder = new StringBuilder();
		topPlayers.values().forEach(
				(topPlayer) -> {
					StringBuilder stringBuilder1 = showEntries(topPlayer);
					if (!stringBuilder1.isEmpty()) {
						stringBuilder.append(showEntries(topPlayer))
								.append("\n");
					}
				}
		);

		if (stringBuilder.isEmpty()) {
			sendMessage(sender, config.lang.notFoundBalance);
			return;
		}

		stringBuilder.setLength(stringBuilder.length() - 1);
		sendMessage(sender, stringBuilder.toString());
	}

	protected StringBuilder showEntries(List<TopResult> topPlayers) {
		String currency = topPlayers.getFirst().getCurrency();

		StringBuilder stringBuilder = new StringBuilder();

		CurrencyType currencyType = CurrencyType.parse(currency);

		stringBuilder.append(config.lang.entryCurrencyBalTop
				.parse("currencyColor", currencyType.getColor())
				.parse("currency", currencyType.getName())
		);

		for (int i = 0; i < topPlayers.size(); i++) {
			TopResult result = topPlayers.get(i);
			switch (i) {
				case 0 -> stringBuilder.append(config.lang.firstPlaceBalTop);
				case 1 -> stringBuilder.append(config.lang.secondPlaceBalTop);
				case 2 -> stringBuilder.append(config.lang.thirdPlaceBalTop);
			}

			stringBuilder.append(config.lang.entryPlayerBalTop
					.parse("place", i + 1)
					.parse("username", result.getUsername())
					.parse("amount", NumberUtils.formatNumberWithUnits(result.getAmount()))
			);
		}

		return stringBuilder;
	}
}
