package gg.mmorealms.module.pokedex_rewards.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.pokedex_rewards.backend.common.gui.PokedexMilestone;
import net.minecraft.world.item.Items;

import java.util.List;

public class PokedexRewardsConfig {

	public String guiHeader = "<red><b>POKEDEX REWARDS";


	public RewardsGUI gui = new RewardsGUI();

	public static class RewardsGUI {
		public GUI.Settings settings = new GUI.Settings()
				.chestSize(4);

		public List<PokedexMilestone> rewards = List.of(
				new PokedexMilestone(
						new GUIButton()
								.display(Items.WHITE_WOOL)
								.displayName("<gradient:red:yellow><b>10% COMPLETION")
								.position(1, 1)
								.lore(List.of(
										"",
										"<white>status:<reset> {status}",
										"",
										"<gradient:gold:yellow:gold><b>REWARDS",
										"<gray>- <aqua>10000 cobble coins",
										"",
										"<white>pokedex progression: <aqua>{count}/{max}",
										"<white>pokedex percentage: <green>{percentage}%"
								)),
						0.1,
						List.of(
								"balance add {user} 10000 PokeCoins"
						),
						3
				),
				new PokedexMilestone(
						new GUIButton()
								.display(Items.PURPLE_WOOL)
								.displayName("<gradient:red:yellow><b>100% COMPLETION")
								.position(2, 5)
								.lore(List.of(
										"",
										"<white>status:<reset> {status}",
										"",
										"<gradient:gold:yellow:gold><b>REWARDS",
										"<gray>- <aqua>100000 cobble coins",
										"<gray>- <aqua>5000 pokebuilder tokens",
										"<gray>- <aqua>3 celestial pouches",
										"<gray>- <aqua>1 shiny pokemon of choice",
										"<gray>- <aqua>1 shiny conversion token",
										"<gray>- <aqua>1 rankup token",
										"",
										"<white>pokedex progression: <aqua>{count}/{max}",
										"<white>pokedex percentage: <green>{percentage}%"
								)),
						1.0,
						List.of(
								"balance add {user} 100000 PokeCoins",
								"balance add {user} 5000 Tokens"
						),
						2
				)
		);

		public List<GUIButton> background = List.of(
				new GUIButton()
						.display(Items.RED_STAINED_GLASS_PANE)
						.position(0, 0, 9, 2),

				new GUIButton()
						.display(Items.BLACK_STAINED_GLASS_PANE)
						.position(2, 0),

				new GUIButton()
						.display(Items.GRAY_STAINED_GLASS_PANE)
						.position(2, 1, 7, 1),

				new GUIButton()
						.display(Items.BLACK_STAINED_GLASS_PANE)
						.position(2, 8),

				new GUIButton()
						.display(Items.WHITE_STAINED_GLASS_PANE)
						.position(3, 0, 9, 1)
		);
	}

	public Lang lang = new Lang();

	public static class Lang {
		public String userNotFound = "<red>User not found";
		public MessageBuilder unknownMilestone = new MessageBuilder("<red>Unknown milestone: {milestone}");
		public MessageBuilder unknownRewardStatus = new MessageBuilder("<red>Unknown status: {status}");
		public String lcokedMilestone = "<red>Unavailable ✗";
		public String availableMilestone = "<green>Available ✔";
		public String claimedMilestone = "<gray>Claimed";
	}

}
