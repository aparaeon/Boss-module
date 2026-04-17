package gg.mmorealms.module.gyms.backend.common.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.gyms.backend.common.dto.gym.Gym;
import gg.mmorealms.module.gyms.backend.common.dto.gym.GymRegion;
import gg.mmorealms.module.gyms.backend.common.dto.trainer.TrainerInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;

public class GymsConfig {
	public Lang lang = new Lang();
	public BattleConfig battleConfig = new BattleConfig();

	public List<GymRegion> regions = List.of(
			new GymRegion("Kanto",
					"region1",
					List.of(
							Gym.builder()
									.name("Pewter City")
									.description(new MessageBuilderList(List.of("Rock type")))
									.gymClausesAsString(List.of(
											"baton_pass",
											"evasion_moves",
											"evasion_abilities",
											"one_hit_knockout",
											"species",
											"bag_clause",
											"smash_pass"
									))
									.spawnLocation(new Location("overworld", 8.5, 60, 4086.5, 0, 90))
									.firstTimeBonusRewards(List.of("poke_give_class {user} NORMAL true"))
									.regularRewardCommands(List.of("give {user} dirt 64"))
									.trainerInfo(new TrainerInfo("brock",
											new Location("overworld", -59.5, 61, 4086.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3014))
									.inactiveBadgeData(new CustomModelData(3014))
									.id("region1_gym1")
									.build(),
							Gym.builder()
									.name("Cerulean City")
									.description(new MessageBuilderList(List.of("Water type")))
									.requiredGyms(List.of("region1_gym1"))
									.spawnLocation(new Location("overworld", 1049.5, 60, 4089.5, 0, 90))
									.firstTimeBonusRewards(List.of("poke_give_class {user} ULTRA_BEAST true"))
									.regularRewardCommands(List.of("give {user} cobblestone 64"))
									.trainerInfo(new TrainerInfo("misty",
											new Location("overworld", 993.5, 58, 4089.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3004))
									.inactiveBadgeData(new CustomModelData(3022))
									.id("region1_gym2")
									.build(),
							Gym.builder()
									.name("Vermilion City")
									.description(new MessageBuilderList(List.of("Electric type")))
									.requiredGyms(List.of("region1_gym2"))
									.spawnLocation(new Location("overworld", 2088.5, 60, 4119.5, 0, 90))
									.firstTimeBonusRewards(List.of("poke_give_class {user} LEGENDARY true"))
									.regularRewardCommands(List.of("give {user} oak_log 64"))
									.trainerInfo(new TrainerInfo("ltsurge",
											new Location("overworld", 2025.5, 60, 4119.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3005))
									.inactiveBadgeData(new CustomModelData(3023))
									.id("region1_gym3")
									.build(),
							Gym.builder()
									.name("Celadon City")
									.description(new MessageBuilderList(List.of("Grass type")))
									.requiredGyms(List.of("region1_gym3"))
									.spawnLocation(new Location("overworld", 3112.5, 60, 4101.5, 0, 90))
									.firstTimeBonusRewards(List.of("poke_give_class {user} MYTHICAL true"))
									.regularRewardCommands(List.of("give {user} iron_block 64"))
									.trainerInfo(new TrainerInfo("erika",
											new Location("overworld", 3030.5, 66, 4101.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3006))
									.inactiveBadgeData(new CustomModelData(3023))
									.id("region1_gym4")
									.build(),
							Gym.builder()
									.name("Fuchsia City")
									.description(new MessageBuilderList(List.of("Poison type")))
									.requiredGyms(List.of("region1_gym4"))
									.spawnLocation(new Location("overworld", 24.5, 60, 3080.5, 0, 90))
									.trainerInfo(new TrainerInfo("koga",
											new Location("overworld", -62.5, 62, 3080.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3009))
									.inactiveBadgeData(new CustomModelData(3027))
									.id("region1_gym5")
									.build(),
							Gym.builder()
									.name("Saffron City")
									.description(new MessageBuilderList(List.of("Psychic type")))
									.requiredGyms(List.of("region1_gym5"))
									.spawnLocation(new Location("overworld", 1052.5, 60, 3064.5, 0, 90))
									.trainerInfo(new TrainerInfo("sabrina",
											new Location("overworld", 980.5, 62, 3064.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3012))
									.inactiveBadgeData(new CustomModelData(3030))
									.id("region1_gym6")
									.build(),
							Gym.builder()
									.name("Cinnabar City")
									.description(new MessageBuilderList(List.of("Fire type")))
									.requiredGyms(List.of("region1_gym6"))
									.spawnLocation(new Location("overworld", 2075.5, 60, 3075.5, 0, 90))
									.trainerInfo(new TrainerInfo("blaine",
											new Location("overworld", 2004.5, 62, 3075.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3003))
									.inactiveBadgeData(new CustomModelData(3021))
									.id("region1_gym7")
									.build(),
							Gym.builder()
									.name("Viridian City")
									.description(new MessageBuilderList(List.of("Ground type")))
									.requiredGyms(List.of("region1_gym7"))
									.spawnLocation(new Location("overworld", 3096.5, 60, 3072.5, 0, 90))
									.trainerInfo(new TrainerInfo("giovanni",
											new Location("overworld", 3026.5, 62, 3072.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3010))
									.inactiveBadgeData(new CustomModelData(3028))
									.id("region1_gym8")
									.build(),
							Gym.builder()
									.name("Lorelei")
									.description(new MessageBuilderList(List.of("Ice type")))
									.requiredGyms(List.of("region1_gym8"))
									.spawnLocation(new Location("overworld", 24.5, 60, 2057.5, 0, 90))
									.trainerInfo(new TrainerInfo("lorelei",
											new Location("overworld", -66.5, 62, 2057.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3007))
									.inactiveBadgeData(new CustomModelData(3025))
									.id("region1_gym9")
									.build(),
							Gym.builder()
									.name("Bruno")
									.description(new MessageBuilderList(List.of("Fighting type")))
									.requiredGyms(List.of("region1_gym9"))
									.spawnLocation(new Location("overworld", 1072.5, 60, 2059.5, 0, 90))
									.trainerInfo(new TrainerInfo("bruno",
											new Location("overworld", 1000.5, 62, 2059.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3008))
									.inactiveBadgeData(new CustomModelData(3026))
									.id("region1_gym10")
									.build(),
							Gym.builder()
									.name("Agatha")
									.description(new MessageBuilderList(List.of("Ghost type")))
									.requiredGyms(List.of("region1_gym10"))
									.spawnLocation(new Location("overworld", 2092.5, 60, 2072.5, 0, 90))
									.trainerInfo(new TrainerInfo("agatha",
											new Location("overworld", 2020.5, 62, 2072.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3015))
									.inactiveBadgeData(new CustomModelData(3033))
									.id("region1_gym11")
									.build(),
							Gym.builder()
									.name("Lance")
									.description(new MessageBuilderList(List.of("Dragon type")))
									.requiredGyms(List.of("region1_gym11"))
									.spawnLocation(new Location("overworld", 3104.5, 60, 2065.5, 0, 90))
									.trainerInfo(new TrainerInfo("lance",
											new Location("overworld", 3032.5, 62, 2065.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3016))
									.inactiveBadgeData(new CustomModelData(3034))
									.id("region1_gym12")
									.build(),
							Gym.builder()
									.name("Blue")
									.description(new MessageBuilderList(List.of("Mix")))
									.requiredGyms(List.of("region1_gym12"))
									.spawnLocation(new Location("overworld", 1688.5, 60, 1401.5, 0, 90))
									.trainerInfo(new TrainerInfo("blue",
											new Location("overworld", 1597.5, 62, 1401.5, 0, -90),
											5))
									.badgeItem(new ItemStack(Items.PAPER))
									.activeBadgeData(new CustomModelData(3014))
									.inactiveBadgeData(new CustomModelData(3032))
									.id("region1_gym13")
									.build()
					),
					new ItemStack(Items.GREEN_WOOL)
			)
	);

	public GymRegionGUI gymRegionGUI = new GymRegionGUI();
	public GymMainGUI gymMainGUI = new GymMainGUI();
	public BadgesGUI badgesGUI = new BadgesGUI();

	public static class Lang {
		public String failedBatonPassClauseMessage = "Failed baton pass clause";
		public String failedBatonPass1ClauseMessage = "Failed baton pass 1 clause";
		public String failedEvasionMovesClauseMessage = "Failed evasion clause";
		public String failedEvasionAbilitiesClauseMessage = "Failed evasion ability clause";
		public String failedOhkoClauseMessage = "Failed OHKO clause";
		public String failedSpeciesClauseMessage = "Failed species clause";
		public String failedChatterClauseMessage = "Failed chatter clause";
		public String failedDrizzleClauseMessage = "Failed drizzle clause";
		public String failedDroughtClauseMessage = "Failed drought clause";
		public String failedDrizzleSwimClauseMessage = "Failed drizzle-swim clause";
		public String failedItemClauseMessage = "Failed item clause";
		public String failedLegendaryClauseMessage = "Failed legendary clause";
		public String failedMoodyClauseMessage = "Failed moody clause";
		public String failedShadowTagClauseMessage = "Failed shadow tag clause";
		public String failedSwaggerClauseMessage = "Failed swagger clause";
		public String failedSmashPassClauseMessage = "Failed smash-pass clause";
		public String failedSandStreamClauseMessage = "Failed sand stream clause";
		public String failedSnowWarningMessage = "Failed snow warning clause";
		public String failedBagClauseMessage = "Failed bag clause";

		public String playerTriedToBattleUnreachableGym = "<red>You did not beat the previous gym yet, how did you getGymRecord here?";
		public String playerNotEnoughInventorySpace = "<red>You don't have enough free space in inventory.";
		public MessageBuilder playerCooldown = new MessageBuilder("<red>This gym is on cooldown after the last {action}. Please wait {cooldown}");

		public String canBattleGymDescription = "<green>You can challenge this gym!";
		public MessageBuilder gymOnCooldownDescription = new MessageBuilder("<yellow>You can battle this gym in {cooldown}");

		public String invalidTargetArgument = "<red>There is no player with this username or the uuid is invalid";
		public MessageBuilder addProgressConfirmation = new MessageBuilder("<green>Successfully added a win for {target} to gym {gym}");
		public MessageBuilder resetProgressConfirmation = new MessageBuilder("<green>Successfully deleted gym record for {target}");

		public String prePlayerRewardMessage = "<green>Some virtual rewards you received:";
	}

	public static class BattleConfig {
		public Time battleTime = Time.minutes(20);
	}

	public static class BadgesGUI {
		public String title = "Badges";

		public List<Integer> slots = List.of(
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24, 25
		);
	}

	public static class GymMainGUI {
		public String title = "Regions";

		public List<GUIButton> background = List.of(
				new GUIButton()
						.display(Items.GRAY_STAINED_GLASS_PANE)
						.position(0, 0, 9, 3)
		);

		public List<Integer> slots = List.of(
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24
		);
	}

	public static class GymRegionGUI {
		public List<GUIButton> background = List.of(
				new GUIButton()
						.display(Items.GRAY_STAINED_GLASS_PANE)
						.position(0, 0, 9, 5)
		);

		public List<Integer> slots = List.of(
				38, 39, 41, 42,
				29, 30, 32, 33,
				11, 12, 14, 15,
				4
		);
	}
}