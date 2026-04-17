package gg.mmorealms.module.economy.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.economy.common.dto.CurrencyType;

import java.util.List;

public class EconomyConfig {

	public List<CurrencyType> trackedCurrencies = List.of(
			CurrencyType.GEMS,
			CurrencyType.TOKENS
	);

	public Lang lang = new Lang();

	public static class Lang {
		public String emptyBalance = "Your balance is empty!";
		public String headerBalance = "=====  Balances  =====\n";
		public MessageBuilder headerCheckBalance = new MessageBuilder("{user} balances:<newline>{balances}");

		public MessageBuilder entryBalance = new MessageBuilder("<dark_grey>▶ {currencyColor}{currency}:<reset> {amount}<newline>");

		public String invalidAmount = "Invalid amount!";
		public String invalidCurrency = "Currency not found!";
		public String invalidUser = "User not found!";

		public MessageBuilder successAddAmount = new MessageBuilder("Successfully added {amount} of {currency} to user {name}");
		public MessageBuilder successRemoveAmount = new MessageBuilder("Successfully removed {amount} of {currency} from user {name}");
		public MessageBuilder successDeleteCurrency = new MessageBuilder("Successfully removed {currency} from user {name}");

	}
}
