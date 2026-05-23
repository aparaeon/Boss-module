package gg.mmorealms.module.login_rewards.velocity.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LoginRewardsModuleConfig {

	public int dailyGuiDaysToShow = 30;
	public boolean autoOpenDailyGui = true;
	/**
	 * When true, days past the highest configured {@link #dailyDays} day wrap back to day 1.
	 * {@link #dailyRewardRules} are ignored in cycle mode.
	 */
	public boolean cycleDailyRewards = false;

	public List<DailyDayConfig> dailyDays = new ArrayList<>(List.of(
		new DailyDayConfig(1, Time.minutes(0), 1,
			List.of("<gray>Instant first-day reward.", "<green>8x Poke Ball", "<gold>250 PokeCoins"),
			new MessageBuilderList(List.of("give {player} cobblemon:poke_ball 8", "bal add {player} 250 PokeCoins")),
			"{\"id\":\"cobblemon:poke_ball\",\"count\":8}"),
		new DailyDayConfig(2, Time.minutes(5), 1,
			List.of("<gray>Requires 5 minutes active playtime.", "<green>16x Poke Ball", "<gold>500 PokeCoins"),
			new MessageBuilderList(List.of("give {player} cobblemon:poke_ball 16", "bal add {player} 500 PokeCoins")),
			"{\"id\":\"cobblemon:poke_ball\",\"count\":16}"),
		new DailyDayConfig(7, Time.minutes(20), 2,
			List.of("<gray>Weekly streak bonus.", "<green>8x Rare Candy", "<gold>2,500 PokeCoins"),
			new MessageBuilderList(List.of("give {player} cobblemon:rare_candy 8", "bal add {player} 2500 PokeCoins")),
			"{\"id\":\"cobblemon:rare_candy\",\"count\":8}"),
		new DailyDayConfig(30, Time.minutes(30), 3,
			List.of("<gray>Monthly streak reward.", "<green>1x Master Ball", "<gold>15,000 PokeCoins"),
			new MessageBuilderList(List.of("give {player} cobblemon:master_ball 1", "bal add {player} 15000 PokeCoins")),
			"{\"id\":\"cobblemon:master_ball\",\"count\":1}")
	));

	/**
	 * Rule-based fallbacks for days not covered by {@link #dailyDays}.
	 * Ignored when {@link #cycleDailyRewards} is true.
	 */
	public List<DailyRewardRuleConfig> dailyRewardRules = new ArrayList<>(List.of(
		new DailyRewardRuleConfig(3, null, null, 0, Time.minutes(10), 1,
			100, 50, 1000,
			List.of("<gray>Daily streak reward.", "<gold>{amount} PokeCoins"),
			new MessageBuilderList(List.of("bal add {player} {amount} PokeCoins"))),
		new DailyRewardRuleConfig(5, null, 5, 10, Time.minutes(15), 2,
			2, 1, 25,
			List.of("<gray>Every fifth day bonus.", "<aqua>{amount} Gems"),
			new MessageBuilderList(List.of("bal add {player} {amount} Gems")))
	));

	public DailyDayConfig defaultDailyDay = new DailyDayConfig(
		0,
		Time.minutes(10),
		1,
		List.of("<gray>Default reward for days not listed above.", "<green>16x Poke Ball", "<gold>1,000 PokeCoins"),
		new MessageBuilderList(List.of("give {player} cobblemon:poke_ball 16", "bal add {player} 1000 PokeCoins")),
		"{\"id\":\"cobblemon:poke_ball\",\"count\":16}"
	);

	public Lang lang = new Lang();

	public DailyDayConfig getDailyDayConfig(int day) {
		int resolvedDay = resolveDailyRewardDay(day);

		DailyDayConfig explicitDay = dailyDays.stream()
			.filter(config -> config.day == resolvedDay)
			.findFirst()
			.orElse(null);

		if (explicitDay != null) {
			return explicitDay;
		}

		return dailyRewardRules.stream()
			.filter(rule -> rule.matches(resolvedDay))
			.max(Comparator.comparingInt(rule -> rule.priority))
			.map(rule -> rule.toDailyDayConfig(resolvedDay))
			.orElse(defaultDailyDay);
	}

	public String getDisplayJson(DailyDayConfig dayConfig) {
		if (dayConfig.displayJson != null && !dayConfig.displayJson.isBlank()) {
			return dayConfig.displayJson;
		}
		if (defaultDailyDay.displayJson != null && !defaultDailyDay.displayJson.isBlank()) {
			return defaultDailyDay.displayJson;
		}
		return "{\"id\":\"minecraft:diamond\",\"count\":1}";
	}

	public int resolveDailyRewardDay(int day) {
		if (!cycleDailyRewards || dailyDays.isEmpty()) {
			return day;
		}

		int maxDay = dailyDays.stream()
			.mapToInt(config -> config.day)
			.max()
			.orElse(0);

		if (maxDay <= 0) {
			return day;
		}

		return ((Math.max(1, day) - 1) % maxDay) + 1;
	}

	@NoArgsConstructor
	public static class DailyDayConfig {
		public int day;
		public Time requiredPlaytime = Time.minutes(10);
		public int requiredInventorySpace = 1;
		public String displayJson = "";
		public List<String> rewardLore = new ArrayList<>();
		public MessageBuilderList commands = new MessageBuilderList(List.of());

		public DailyDayConfig(int day, Time requiredPlaytime, int requiredInventorySpace,
		                      List<String> rewardLore, MessageBuilderList commands,
		                      String displayJson) {
			this.day = day;
			this.requiredPlaytime = requiredPlaytime;
			this.requiredInventorySpace = requiredInventorySpace;
			this.displayJson = displayJson;
			this.rewardLore = rewardLore;
			this.commands = commands;
		}

		public long getRequiredPlaytimeMs() {
			if (requiredPlaytime == null) {
				return 0L;
			}

			return requiredPlaytime.toMilliseconds();
		}
	}

	@NoArgsConstructor
	public static class DailyRewardRuleConfig {
		public int fromDay = 1;
		public Integer toDay;
		public Integer everyNthDay;
		public int priority = 0;
		public Time requiredPlaytime = Time.minutes(10);
		public int requiredInventorySpace = 1;
		public int baseAmount = 0;
		public int incrementPerDay = 0;
		public Integer maxAmount;
		public String displayJson = "";
		public List<String> rewardLore = new ArrayList<>();
		public MessageBuilderList commands = new MessageBuilderList(List.of());

		public DailyRewardRuleConfig(int fromDay, Integer toDay, Integer everyNthDay, int priority,
		                             Time requiredPlaytime, int requiredInventorySpace,
		                             int baseAmount, int incrementPerDay, Integer maxAmount,
		                             List<String> rewardLore, MessageBuilderList commands) {
			this.fromDay = fromDay;
			this.toDay = toDay;
			this.everyNthDay = everyNthDay;
			this.priority = priority;
			this.requiredPlaytime = requiredPlaytime;
			this.requiredInventorySpace = requiredInventorySpace;
			this.baseAmount = baseAmount;
			this.incrementPerDay = incrementPerDay;
			this.maxAmount = maxAmount;
			this.rewardLore = rewardLore;
			this.commands = commands;
		}

		public boolean matches(int day) {
			if (day < fromDay) {
				return false;
			}

			if (toDay != null && day > toDay) {
				return false;
			}

			return everyNthDay == null || everyNthDay <= 1 || (day - fromDay) % everyNthDay == 0;
		}

		public DailyDayConfig toDailyDayConfig(int day) {
			int amount = baseAmount + ((day - fromDay) * incrementPerDay);

			if (maxAmount != null) {
				amount = Math.min(amount, maxAmount);
			}

			return new DailyDayConfig(
				day,
				requiredPlaytime,
				requiredInventorySpace,
				parseLines(rewardLore, day, amount),
				new MessageBuilderList(commands
					.parse("day", day)
					.parse("amount", amount)
					.parse()),
				displayJson
			);
		}

		private static List<String> parseLines(List<String> lines, int day, int amount) {
			return lines.stream()
				.map(line -> line
					.replace("{day}", String.valueOf(day))
					.replace("{amount}", String.valueOf(amount)))
				.toList();
		}
	}

	public static class Lang {
		public MessageBuilder playtimeInfo = new MessageBuilder(
			"<gray>Active playtime: <white>{playtime}<gray>."
		);
		public MessageBuilder dailyClaimed = new MessageBuilder(
			"<green>✔ <white>Day <gold>{day}</gold> claimed! <gray>Streak: <gold>{streak}<gray>."
		);
		public MessageBuilder alreadyClaimed = new MessageBuilder(
			"<yellow>⚠ <white>You've already claimed today's reward."
		);
		public MessageBuilder notEnoughPlaytime = new MessageBuilder(
			"<red>✗ <white>Need <gold>{needed}</gold> more playtime for day <gold>{day}</gold>."
		);
		public MessageBuilder streakBroken = new MessageBuilder(
			"<red>✗ <white>Streak broken — reset to day 1."
		);
		public MessageBuilder streakInfo = new MessageBuilder("""
			
			<gold><bold>✦ Daily Streak</bold>
			<dark_gray>──────────────────
			<gray> Streak    <white>{streak}
			<gray> Playtime  <white>{today} <dark_gray>/ <gold>{required}
			<gray> Next Day  <white>Day {day}
			<gray> Available <white>{available_in}
			<gray> Expires   <white>{claim_window}""");
		public MessageBuilder streakClaimedInfo = new MessageBuilder("""
			
			<gold><bold>✦ Daily Streak</bold>
			<dark_gray>──────────────────
			<gray> Streak    <white>{streak} <green>✔ claimed
			<gray> Next Day  <white>Day {day}
			<gray> Available <white>{available_in}
			<gray> Expires   <white>{claim_window}""");
		public MessageBuilder checkInfo = new MessageBuilder("""
			
			<gold><bold>✦ {player}</bold> <gray>({online}<gray>)
			<dark_gray>──────────────────
			<gray> Streak    <white>{streak}{previous_streak_hint}
			<gray> Today     <white>{today} <dark_gray>/ <gold>{required}
			<gray> Next Day  <white>Day {day}
			<gray> Last Claim <white>{last_claim_age} <gray>ago
			<gray> Available <white>{available_in}
			<gray> Window    <white>{claim_window}""");
		public MessageBuilder playerNotFound = new MessageBuilder(
			"<red>✗ <white>Player not found: <gray>{player}<gray>."
		);
		public MessageBuilder invalidAmount = new MessageBuilder(
			"<red>✗ <white>Amount must be a non-zero whole number."
		);
		public MessageBuilder adminModified = new MessageBuilder(
			"<gray>{player}: <white>{previous} <gray>→ <white>{streak} <dark_gray>(<yellow>{delta_sign}{delta}<dark_gray>)<gray>."
		);
		public MessageBuilder claimFailed = new MessageBuilder(
			"<red>✗ <white>Claim failed. Please try again."
		);
		public MessageBuilder dailyReady = new MessageBuilder(
			"<green>✦ <white>Daily reward ready! Use <gold>/daily</gold> to claim."
		);
	}
}
