package gg.mmorealms.module.tebex_integration.velocity.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.tebex_integration.velocity.dto.TebexDiscounts;

import java.util.List;

public class TebexIntegrationConfig {

	public String tebexToken = "0a9cddbb490901bef55d0fea40612d2cea42c3b4";

	public Time reminderTimer = Time.minutes(10);

	public List<TebexDiscounts> discounts = List.of(
			new TebexDiscounts(10, 5, Time.hours(1)),
			new TebexDiscounts(25, 10, Time.hours(2)),
			new TebexDiscounts(50, 15, Time.hours(3)),
			new TebexDiscounts(100, 20, Time.hours(4)),
			new TebexDiscounts(200, 25, Time.hours(5))
	);

	public String discountCodeDictionary = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public int discountCodeLength = 8;

	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilderList closeMessage = new MessageBuilderList(List.of(
				"<reset>",
				"<reset>",
				"<white><b>================= <aqua><b>STORE <white><b>=================<reset>",
				"<reset>",
				"<white>You are close to a discount for your future payment!<reset>",
				"<white>Purchase <gold><b>${remaining} <reset>more to unlock a <gold><b>{discount}% <reset>discount for your next payment!<reset>",
				"<reset>",
				"<white><b>================= <aqua><b>STORE <white><b>=================<reset>",
				"<reset>",
				"<reset>"
		));

		public MessageBuilderList discountMessage = new MessageBuilderList(List.of(
				"<reset>",
				"<reset>",
				"<white><b>================= <aqua><b>STORE <white><b>=================<reset>",
				"<reset>",
				"<aqua><b>Congratulations!<reset>",
				"<white>You have unlocked a <gold><b>{discount}% <reset>discount for your next payment!<reset>",
				"<white>Use the code <click:copy_to_clipboard:'{code}'><gold><b>{code} <green>(Click to copy)</click> <reset>at checkout to apply your discount!<reset>",
				"<aqua><b>Hurry up! <reset><white>This discount is only valid for <gold><b>{duration} <reset>after you receive this message!<reset>",
				"<reset>",
				"<white><b>================= <aqua><b>STORE <white><b>=================<reset>",
				"<reset>",
				"<reset>"
		));
	}


}
