package gg.mmorealms.module.gambling.backend.common.files;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.generic.dto.Range;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.gambling.backend.common.dto.RouletteSlotType;

import java.util.HashMap;
import java.util.List;

public class GambleConfig {

	public GambleGUI gambleGUI = new GambleGUI();
	public RouletteGUI rouletteGUI = new RouletteGUI();

	public List<CurrencyType> allowedCurrencies = List.of(
			CurrencyType.POKECOINS
	);
	public Time autoCloseDelay = Time.seconds(3);

	public static class GambleGUI {
		public GUIButton redButton = GUIButton.empty()
				.position(0, 0, 3, 3)
				.displayName("<red><b>Red")
				.lore(List.of(
						"<gray>",
						"<gray>This option has a <gold>12/26 <gray>(~<gold>46.15%<gray>) chance of winning.",
						"<gray>Pays <gold>2:1 <gray>on win (<gold>2x<gray>).",
						"<gray>",
						"<aqua>Click to select."
				));
		public GUIButton greenButton = GUIButton.empty()
				.position(0, 3, 3, 3)
				.displayName("<green><b>Green")
				.lore(List.of(
						"<gray>This option has a <gold>2/26 <gray>(~<gold>7.69%<gray>) chance of winning.",
						"<gray>Pays <gold>10:1 <gray>on win (<gold>10x<gray>).",
						"<gray>",
						"<aqua>Click to select."
				));
		public GUIButton blackButton = GUIButton.empty()
				.position(0, 6, 3, 3)
				.displayName("<gray><b>Black")
				.lore(List.of(
						"<gray>",
						"<gray>This option has a <gold>12/26 <gray>(~<gold>46.15%<gray>) chance of winning.",
						"<gray>Pays <gold>2:1 <gray>on win (<gold>2x<gray>).",
						"<gray>",
						"<aqua>Click to select."
				));
		public GUIButton setCurrencyButton = GUIButton.empty()
				.position(4, 0)
				.displayName("<gold><b>Set currency")
				.lore(List.of(
						"<gray>",
						"<gray>Currency bet currency: <gold>{currency}.",
						"<gray>",
						"<aqua>Click to select."
				));
		public GUIButton setBetButton = GUIButton.empty()
				.position(4, 1, 3, 1)
				.displayName("<gold><b>Set bet")
				.lore(List.of(
						"<gray>",
						"<gray>Select the amount you want to bet.",
						"<gray>Current bet amount: <gold>{amount} {currency}.",
						"<gray>",
						"<aqua>Click to select."
				));
		public GUIButton playButton = GUIButton.empty()
				.position(4, 5, 3, 1)
				.displayName("<gold><b>Play")
				.lore(List.of(
						"<gray>",
						"<gray>Current bet amount: <gold>{amount} {currency}.",
						"<gray>Current bet on {slot_type}.",
						"<gray>",
						"<aqua>Click to play."
				));

	}

	public static class RouletteGUI {

		public List<Integer> allSlots = List.of(
				0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 53, 52, 51, 50, 49, 48, 47, 46, 45, 36, 27, 18, 9
		);
		public List<Integer> redSlots = List.of(0, 2, 5, 7, 17, 35, 53, 51, 48, 46, 36, 18);
		public List<Integer> blackSlots = List.of(1, 3, 6, 8, 26, 45, 52, 50, 47, 44, 27, 9);
		public List<Integer> greenSlots = List.of(4, 49);

		public List<Pair2<Range, Integer>> animationDelays = List.of(
				new Pair2<>(Range.of(0, 40), 1),
				new Pair2<>(Range.of(41, 80), 2),
				new Pair2<>(Range.of(81, 90), 5),
				new Pair2<>(Range.of(91, 95), 10)
		);

		private transient Integer maxProgress = null;

		public Integer getMaxProgress() {
			if (maxProgress == null) {
				maxProgress = animationDelays.stream().map(Pair2::first).map(Range::getMax).max(Integer::compareTo).orElse(0);
			}

			return maxProgress;
		}

		public HashMap<RouletteSlotType, Double> winMultipliers = new HashMap<>() {{
			put(RouletteSlotType.RED, 2.0);
			put(RouletteSlotType.BLACK, 2.0);
			put(RouletteSlotType.GREEN, 10.0);
			put(RouletteSlotType.UNKNOWN, 0.0);
		}};
	}

}
