package gg.mmorealms.module.chat_games.velocity.config;

import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatGamesConfig {

	public List<CustomQuestion> customQuestions = new ArrayList<>(List.of(new CustomQuestion()));
	public MathConfig math = new MathConfig();

	public static class MathConfig {
		public Range addends = new Range(1, 1000);
		public Range factors = new Range(2, 25);
	}

	public int roundDurationSeconds = 30;
	public Range intervalMinutes = new Range(8, 12);

	public List<ChatGamesReward> rewards = List.of(
		new ChatGamesReward(1.0,
			new MessageBuilder("<green>You won 500 coins!"),
			new MessageBuilderList(List.of("eco give {player} 500"))),
		new ChatGamesReward(0.5,
			new MessageBuilder("<gold>You won a rare reward!"),
			new MessageBuilderList(List.of("give {player} diamond 1")))
	);

	public Map<String, MessageBuilder> questionPrefixes = new LinkedHashMap<>() {{
		put("unscramble_pokemon", new MessageBuilder("<light_purple><bold>✦ Unscramble It!</bold>"));
		put("unscramble_ability", new MessageBuilder("<aqua><bold>✦ Unscramble It!</bold>"));
		put("unscramble_move", new MessageBuilder("<aqua><bold>✦ Unscramble It!</bold>"));
		put("unscramble_nature", new MessageBuilder("<aqua><bold>✦ Unscramble It!</bold>"));
		put("dex_entry", new MessageBuilder("<yellow><bold>✦ Who's That Pokémon?</bold>"));
		put("pokemon_type", new MessageBuilder("<green><bold>✦ Name That Type!</bold>"));
		put("type_pokemon", new MessageBuilder("<green><bold>✦ Name That Pokémon!</bold>"));
		put("pokemon_ability", new MessageBuilder("<gold><bold>✦ Name That Ability!</bold>"));
		put("ability_pokemon", new MessageBuilder("<gold><bold>✦ Name That Pokémon!</bold>"));
		put("pokemon_form", new MessageBuilder("<red><bold>✦ Name That Pokémon!</bold>"));
		put("egg_group_pokemon", new MessageBuilder("<blue><bold>✦ Egg Group!</bold>"));
		put("custom", new MessageBuilder("<light_purple><bold>✦ Trivia</bold>"));
		put("math", new MessageBuilder("<aqua><bold>✦ Quick Maths!</bold>"));
		put("default", new MessageBuilder("<light_purple><bold>✦ Trivia</bold>"));
	}};

	public Map<String, MessageBuilder> questionBroadcasts = new LinkedHashMap<>() {{
		put("unscramble_pokemon", new MessageBuilder("<newline><dark_gray> ▎ <light_purple><bold>✦ Unscramble It!</bold><newline><dark_gray> ▎ <white>What Pokémon is <bold>{question}</bold>?<newline>"));
		put("unscramble_ability", new MessageBuilder("<newline><dark_gray> ▎ <aqua><bold>✦ Unscramble It!</bold><newline><dark_gray> ▎ <white>What ability is <bold>{question}</bold>?<newline>"));
		put("unscramble_move", new MessageBuilder("<newline><dark_gray> ▎ <aqua><bold>✦ Unscramble It!</bold><newline><dark_gray> ▎ <white>What move is <bold>{question}</bold>?<newline>"));
		put("unscramble_nature", new MessageBuilder("<newline><dark_gray> ▎ <aqua><bold>✦ Unscramble It!</bold><newline><dark_gray> ▎ <white>What nature is <bold>{question}</bold>?<newline>"));
		put("dex_entry", new MessageBuilder("<newline><dark_gray> ▎ <yellow><bold>✦ Who's That Pokémon?</bold><newline><dark_gray> ▎ <white>{question}<newline>"));
		put("pokemon_type", new MessageBuilder("<newline><dark_gray> ▎ <green><bold>✦ Name That Type!</bold><newline><dark_gray> ▎ <white>What type(s) is <bold>{question}</bold>?<newline>"));
		put("type_pokemon", new MessageBuilder("<newline><dark_gray> ▎ <green><bold>✦ Name That Pokémon!</bold><newline><dark_gray> ▎ <white>Name a <bold>{question}</bold> type Pokémon<newline>"));
		put("pokemon_ability", new MessageBuilder("<newline><dark_gray> ▎ <gold><bold>✦ Name That Ability!</bold><newline><dark_gray> ▎ <white>Name an ability of <bold>{question}</bold><newline>"));
		put("ability_pokemon", new MessageBuilder("<newline><dark_gray> ▎ <gold><bold>✦ Name That Pokémon!</bold><newline><dark_gray> ▎ <white>Name a Pokémon with the <bold>{question}</bold> ability<newline>"));
		put("pokemon_form", new MessageBuilder("<newline><dark_gray> ▎ <red><bold>✦ Name That Pokémon!</bold><newline><dark_gray> ▎ <white>Name a Pokémon with a <bold>{question}</bold> form<newline>"));
		put("egg_group_pokemon", new MessageBuilder("<newline><dark_gray> ▎ <blue><bold>✦ Egg Group!</bold><newline><dark_gray> ▎ <white>Name a Pokémon in the <bold>{question}</bold> egg group<newline>"));
		put("custom", new MessageBuilder("<newline><dark_gray> ▎ <light_purple><bold>✦ Trivia</bold><newline><dark_gray> ▎ <white>{question}<newline>"));
		put("math", new MessageBuilder("<newline><dark_gray> ▎ <aqua><bold>✦ Quick Maths!</bold><newline><dark_gray> ▎ <white>What is <bold>{question}</bold>?<newline>"));
		put("default", new MessageBuilder("<newline><dark_gray> ▎ <light_purple><bold>✦ Trivia</bold><newline><dark_gray> ▎ <white>{question}<newline>"));
	}};

	public MessageBuilder fallbackBroadcast = new MessageBuilder(
		"<newline><dark_gray> ▎ <light_purple><bold>✦ Trivia</bold><newline><dark_gray> ▎ <white>{question}<newline>"
	);

	public Season season = new Season();

	public Lang lang = new Lang();

	public static class Season {

		public boolean enabled = true;
		public int resetDayOfMonth = 1;
		public String timezone = "UTC";
		public int leaderboardSize = 100;
		public String seasonLeaderboardTitle = "Monthly Leaderboard";

		public List<SeasonReward> monthlyRewards = List.of(
			new SeasonReward(1, 1,
				new MessageBuilder("<newline><dark_gray> ▎ <gold><bold>✦ Season End</bold> <dark_gray>— <yellow>🏆 <bold>{player}</bold> <gray>is the Season Champion with <white>{wins} wins<gray>!<newline>"),
				new MessageBuilderList(List.of(
					"poke_give_class {player} LEGENDARY true",
					"plushies give_class {player} MYTHICAL true",
					"crates admin give_virtual_key {player} 1 premium",
					"bal add {player} 750 Gems"
				)),
				List.of(
					"<gray>● <white>1x Shiny Legendary Pokemon",
					"<gray>● <white>1x Shiny Mythical Plushie",
					"<gray>● <white>1x Premium Crate Key",
					"<gray>● <aqua>750 Gems"
				)),
			new SeasonReward(2, 2,
				new MessageBuilder("<newline><dark_gray> ▎ <gold><bold>✦ Season End</bold> <dark_gray>— <white><bold>{player}</bold> <gray>finished <gold>#{placement}</gold> with <white>{wins} wins<gray>!<newline>"),
				new MessageBuilderList(List.of(
					"poke_give_class {player} LEGENDARY false",
					"plushies give_class {player} LEGENDARY true",
					"crates admin give_virtual_key {player} 1 premium",
					"bal add {player} 500 Gems"
				)),
				List.of(
					"<gray>● <white>1x Legendary Pokemon",
					"<gray>● <white>1x Random Shiny Legendary Plushie",
					"<gray>● <white>1x Premium Crate Key",
					"<gray>● <aqua>500 Gems"
				)),
			new SeasonReward(3, 3,
				new MessageBuilder("<newline><dark_gray> ▎ <gold><bold>✦ Season End</bold> <dark_gray>— <white><bold>{player}</bold> <gray>finished <gold>#{placement}</gold> with <white>{wins} wins<gray>!<newline>"),
				new MessageBuilderList(List.of(
					"poke_give_class {player} ULTRA_BEAST true",
					"plushies give_class {player} LEGENDARY false",
					"crates admin give_virtual_key {player} 2 basic",
					"bal add {player} 250 Gems"
				)),
				List.of(
					"<gray>● <white>1x Shiny Ultra Beast Pokemon",
					"<gray>● <white>1x Legendary Plushie",
					"<gray>● <white>2x Basic Crate Keys",
					"<gray>● <aqua>250 Gems"
				))
		);

		public MessageBuilder seasonEndHeader = new MessageBuilder(
			"<newline><dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<newline><dark_gray> ▎ <gold><bold>✦ Season Over!</bold> <gray>Here are the top winners:<newline>"
		);

		public MessageBuilder seasonEndFooter = new MessageBuilder(
			"<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<newline>"
		);

		public MessageBuilder noWinnersMessage = new MessageBuilder(
			"<newline><dark_gray> ▎ <gold><bold>✦ Season Over!</bold> <gray>No players participated this season.<newline>"
		);

	}

	public static class Lang {

		public MessageBuilder winnerBroadcast = new MessageBuilder(
			"<newline><dark_gray> ▎ {prefix} <dark_gray>— <green><bold>{player}</bold> <gray>got it! <gray>Answer was <green>{answer} <dark_gray>· <aqua>{time}s<newline>"
		);

		public MessageBuilder expiredBroadcast = new MessageBuilder(
			"<newline><dark_gray> ▎ {prefix} <dark_gray>— <gray>Nobody got it. The answer was <green>{answer}<gray>.<newline>"
		);

		public MessageBuilder adminForced = new MessageBuilder(
			"<gray>Forced a new chat game round."
		);

		public MessageBuilder adminSkipped = new MessageBuilder(
			"<gray>Skipped the current question."
		);

		public MessageBuilder adminNothingToSkip = new MessageBuilder(
			"<gray>No active question to skip."
		);

		public MessageBuilder adminBroadcasted = new MessageBuilder(
			"<gray>Question broadcasted."
		);

		public MessageBuilder adminQuestionAlreadyActive = new MessageBuilder(
			"<red>A question is already active. Skip it first."
		);

		public MessageBuilder unplacedLabel = new MessageBuilder(
			"Unplaced"
		);

		public MessageBuilder noRewardToClaim = new MessageBuilder(
			"<red>You have no unclaimed season rewards."
		);

		public MessageBuilder adminPointGiven = new MessageBuilder(
			"<green>Gave <white>{amount}</white> point(s) to <white>{ign}</white>."
		);

		public MessageBuilder adminPointRemoved = new MessageBuilder(
			"<green>Removed <white>{amount}</white> point(s) from <white>{ign}</white>."
		);

		public MessageBuilder adminPlayerNotFound = new MessageBuilder(
			"<red>Player <white>{ign}</white> not found."
		);

		public MessageBuilder adminInvalidAmount = new MessageBuilder(
			"<red>Amount must be a positive number."
		);

		public MessageBuilder toggledOn = new MessageBuilder(
			"<newline><dark_gray> ▎ <light_purple>✦ Chat Games <dark_gray>— <green><bold>Notifications</bold> <bold>enabled</bold><gray>. You will now see trivia questions.<newline>"
		);

		public MessageBuilder toggledOff = new MessageBuilder(
			"<newline><dark_gray> ▎ <light_purple>✦ Chat Games <dark_gray>— <red><bold>Notifications</bold> <bold>disabled</bold><gray>. You will no longer see trivia questions.<newline>"
		);

		public MessageBuilder help = new MessageBuilder(
			"<newline><dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<newline>" +
				"<dark_gray> ▎ <light_purple><bold>✦ Chat Games</bold><newline>" +
				"<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<newline>" +
				"<dark_gray> ▎ <white>/cg leaderboard <dark_gray>— <gray>View the leaderboards<newline>" +
				"<dark_gray> ▎ <white>/cg claim <dark_gray>— <gray>Claim your season reward<newline>" +
				"<dark_gray> ▎ <white>/cg mute <dark_gray>— <gray>Toggle trivia notifications<newline>" +
				"<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━<newline>"
		);

		public MessageBuilder multipleRewardsPending = new MessageBuilder(
			"<yellow>You have <white>{count}</white> unclaimed season rewards. Claim them one at a time with <white>/cg claim</white>."
		);

	}

}
