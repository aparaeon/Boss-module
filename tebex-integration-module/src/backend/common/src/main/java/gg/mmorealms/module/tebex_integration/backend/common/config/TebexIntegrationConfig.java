package gg.mmorealms.module.tebex_integration.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.tebex_integration.backend.common.dto.TebexMilestone;
import net.minecraft.world.item.Items;

import java.util.List;

public class TebexIntegrationConfig {

	public List<TebexMilestone> monthlyMilestones = List.of(
			new TebexMilestone(
					"monthly_1",
					"<white>Monthly Milestone 1",
					10,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.1"
					))
			),
			new TebexMilestone(
					"monthly_2",
					"<white>Monthly Milestone 2",
					25,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.2"
					))
			),
			new TebexMilestone(
					"monthly_3",
					"<white>Monthly Milestone 3",
					50,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.3"
					))
			),
			new TebexMilestone(
					"monthly_4",
					"<white>Monthly Milestone 4",
					100,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.4"
					))
			),
			new TebexMilestone(
					"monthly_5",
					"<white>Monthly Milestone 5",
					150,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.5"
					))
			),
			new TebexMilestone(
					"monthly_6",
					"<white>Monthly Milestone 6",
					200,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.6"
					))
			),
			new TebexMilestone(
					"monthly_7",
					"<white>Monthly Milestone 7",
					250,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.7"
					))
			),
			new TebexMilestone(
					"monthly_8",
					"<white>Monthly Milestone 8",
					300,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.8"
					))
			),
			new TebexMilestone(
					"monthly_9",
					"<white>Monthly Milestone 9",
					350,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.9"
					))
			),
			new TebexMilestone(
					"monthly_10",
					"<white>Monthly Milestone 10",
					400,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.10"
					))
			),
			new TebexMilestone(
					"monthly_11",
					"<white>Monthly Milestone 11",
					350,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.11"
					))
			),
			new TebexMilestone(
					"monthly_12",
					"<white>Monthly Milestone 12",
					500,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 1.12"
					))
			)
	);
	public List<TebexMilestone> alltimeMilestones = List.of(
			new TebexMilestone(
					"lifetime_1",
					"<white>Yearly Milestone 1",
					50,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.1"
					))
			),
			new TebexMilestone(
					"lifetime_2",
					"<white>Yearly Milestone 2",
					100,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.2"
					))
			),
			new TebexMilestone(
					"lifetime_3",
					"<white>Yearly Milestone 3",
					200,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.3"
					))
			),
			new TebexMilestone(
					"lifetime_4",
					"<white>Yearly Milestone 4",
					300,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.4"
					))
			),
			new TebexMilestone(
					"lifetime_5",
					"<white>Yearly Milestone 5",
					400,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.5"
					))
			),
			new TebexMilestone(
					"lifetime_6",
					"<white>Yearly Milestone 6",
					500,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.6"
					))
			),
			new TebexMilestone(
					"lifetime_7",
					"<white>Yearly Milestone 7",
					600,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.7"
					))
			),
			new TebexMilestone(
					"lifetime_8",
					"<white>Yearly Milestone 8",
					700,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.8"
					))
			),
			new TebexMilestone(
					"lifetime_9",
					"<white>Yearly Milestone 9",
					800,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.9"
					))
			),
			new TebexMilestone(
					"lifetime_10",
					"<white>Yearly Milestone 10",
					900,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.10"
					))
			),
			new TebexMilestone(
					"lifetime_11",
					"<white>Yearly Milestone 11",
					1000,
					" <white>Reward description goes here",

					new MessageBuilderList(List.of(
							"say Milestone 2.11"
					))
			),
			new TebexMilestone(
					"lifetime_12",
					"<white>Yearly Milestone 12",
					1500,
					" <white>Reward description goes here",
					new MessageBuilderList(List.of(
							"say Milestone 2.12"
					))
			)
	);

	public RewardsGUI rewardsGUI = new RewardsGUI();
	public RewardsMainMenuGUI rewardsMainMenuGUI = new RewardsMainMenuGUI();

	public static class RewardsMainMenuGUI {
		public String title = "<aqua><bold>Rewards";
		public GUI.Settings settings = new GUI.Settings()
				.chestSize(6);

		public GUIButton background = new GUIButton()
				.displayName("")
				.display(Items.BLUE_STAINED_GLASS_PANE)
				.position(0, 0, 9, 6);


		public GUIButton purchaseMonthlyRewards = new GUIButton()
				.displayName("<green><bold>Monthly Rewards")
				.position(2, 1, 2, 2)
				.display(Items.LIME_STAINED_GLASS_PANE)
				.lore(
						"",
						"<gray>View and collect your monthly rewards!"
				);

		public GUIButton purchaseAllTimeRewards = new GUIButton()
				.displayName("<green><bold>All-Time Rewards")
				.position(2, 6, 2, 2)
				.display(Items.PINK_STAINED_GLASS_PANE)
				.lore(
						"",
						"<gray>View and collect your all-time rewards!"
				);

	}

	public static class RewardsGUI {
		public String title = "<aqua><bold>Rewards";
		public GUI.Settings settings = new GUI.Settings()
				.chestSize(6);

		public List<Integer> slots = List.of(
				19, 20, 21, 22, 23, 24, 25,
				29, 30, 31, 32, 33
		);

		public GUIButton background = new GUIButton()
				.displayName("")
				.display(Items.BLUE_STAINED_GLASS_PANE)
				.position(0, 0, 9, 6);

		public GUIButton collectedReward = new GUIButton()
				.displayName("{name}")
				.lore(
						"",
						"{description}",
						"",
						"<green>${current}/${target} spent",
						"<gray>Already collected!"
				)
				.display(Items.GRAY_CONCRETE);

		public GUIButton availableReward = new GUIButton()
				.displayName("{name}")
				.lore(
						"",
						"{description}",
						"",
						"<green>${current}/${target} spent",
						"<green>Click to collect!"
				)
				.display(Items.GREEN_CONCRETE);

		public GUIButton unavailableReward = new GUIButton()
				.displayName("{name}")
				.lore(
						"",
						"{description}",
						"",
						"<green>${current}/${target} spent",
						"<red>Not yet available!"
				)
				.display(Items.RED_CONCRETE);


		public GUIButton back = new GUIButton()
				.displayName("Go back to main menu")
				.position(49)
				.lore(
						"",
						"Click to go back to the main menu!"
				)
				.display(Items.BARRIER);
	}

}
