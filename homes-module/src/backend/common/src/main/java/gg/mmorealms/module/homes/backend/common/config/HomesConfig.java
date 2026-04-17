package gg.mmorealms.module.homes.backend.common.config;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import net.minecraft.world.item.Items;

import java.util.List;

public class HomesConfig {
	public MessageBuilder homesPermission = new MessageBuilder("mmorealms.homes.{number}");

	public HomesGUI homesGUI = new HomesGUI();

	public Lang lang = new Lang();

	public static class HomesGUI {
		public List<GUIButton> background = List.of(
				new GUIButton()
						.display(Items.WHITE_STAINED_GLASS_PANE)
						.position(
								0, 1, 2, 3, 4, 5, 6, 7, 8,
								9, 17, 18, 26, 27, 35, 36, 44,
								45, 46, 47, 49, 51, 52, 53
						),
				new GUIButton()
						.display(Items.REDSTONE_TORCH)
						.position(48)
						.displayName("<gold><bold><underlined>WARNING")
						.lore(List.of("When wild servers restart,",
								"their world resets.",
								"We highly recommend avoiding",
								"setting any permanent homes",
								"on those servers",
								""))
		);

		public GUIButton nextPageItem = new GUIButton()
				.display(Items.ARROW)
				.displayName("<b><white>Next →")
				.position(5, 7);

		public GUIButton previousPageItem = new GUIButton()
				.display(Items.ARROW)
				.displayName("<b><white>← Previous")
				.position(5, 1);

		public List<Integer> slots = List.of(
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24, 25,
				28, 29, 30, 31, 32, 33, 34,
				37, 38, 39, 40, 41, 42, 43
		);

		public GUIButton addHome = new GUIButton()
				.display(Items.TORCH)
				.position(50)
				.displayName("<green><bold>Add a new home");

		public GUIButton assignedHome = new GUIButton()
				.display(Items.GREEN_STAINED_GLASS_PANE)
				.displayName("<green>{home_name}")
				.lore(List.of("",
						"<green>Left Click to teleport",
						"<red>Right Click to delete"
				));

		public GUIButton unavailableHome = new GUIButton()
				.display(Items.RED_STAINED_GLASS_PANE)
				.displayName("<red>Unavailable");

		public GUIButton availableHome = new GUIButton()
				.display(Items.YELLOW_STAINED_GLASS_PANE)
				.displayName("<yellow>Available");
	}

	public static class Lang {
		public MessageBuilder titleHomesGUI = new MessageBuilder("<grey>Homes <gold>{page}");

		public String messageAddHomeHomesGUI = "<underlined><click:suggest_command:'/home add '><green>/home add " +
				"<name><newline>";

		public String maxHomes = "<red>You reached your maximum number of assignable homes!";
		public String alreadyExistsHome = "<red>You already have a home with this name!";
		public String notFoundRealmHome = "<red>We can't locate the realm you are in, please make sure that the owner of this realm is online";
		public MessageBuilder successAddHome = new MessageBuilder("<green>Successfully added {name} as a home!");

		public String teleportToOfflinePlayerRealmHome = "<red>The player that has this realm is not online!";

		public String warningTeleportingToWildHome = "<red><bold><underlined>WARNING!</underlined></bold></red>" +
				"<newline>" +
				"<gold>You are about to teleport to a home set in the wilderness. If the destination isn't as expected, please delete this home, as the wild server has been reset.";

		public MessageBuilder noHomeMessageListCommand = new MessageBuilder("<red>{user} doesn't have any home set!");
		public MessageBuilder headerListCommand = new MessageBuilder("=====  {user}'s Homes  =====<newline>");

		public MessageBuilder entryListCommand = new MessageBuilder("<dark_grey>▶ {name}<newline>");

		public String noHomeDeleteCommand = "<red>You have no home with this name!";
		public MessageBuilder successDeleteCommand = new MessageBuilder("<green>Successfully deleted {name} home!");

		public MessageBuilder noHomeWithThisNameAdminCommand = new MessageBuilder("<red>{user} does not have a home named {name}!");
		public MessageBuilder successDeleteAdminCommand = new MessageBuilder("<green>Successfully deleted {name} home for user {user}!");
		public MessageBuilder successDeleteAllAdminCommand = new MessageBuilder("<green>Successfully deleted all homes for {user}");

		public String errorRealmNotFound = "<red>You don't have a realm!";
		public String errorRealmNotLoaded = "<red>Realm not loaded, please wait!";
		public String invalidServerType = "<red>Can't determine server type, please contact an administrator.";
	}
}
