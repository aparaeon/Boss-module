package gg.mmorealms.module.economy.velocity.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;

import java.util.List;

public class EconomyConfig {
	public List<String> currenciesToMakeTopFor = List.of("POKECOINS", "TOKENS");

	public Lang lang = new Lang();

	public static class Lang {
		public String headerBalTop = "=====  Top Balances  =====";
		public String notFoundBalance = "There are no balances yet!";

		public String firstPlaceBalTop = "<#FFC536>";
		public String secondPlaceBalTop = "<#CBCBCB>";
		public String thirdPlaceBalTop = "<#ED9D5D>";

		public MessageBuilder entryCurrencyBalTop = new MessageBuilder("<dark_grey>▶ {currencyColor}{currency}<reset><newline>");

		public MessageBuilder entryPlayerBalTop = new MessageBuilder("{place}. {username}: <reset>{amount}<newline>");
	}
}
