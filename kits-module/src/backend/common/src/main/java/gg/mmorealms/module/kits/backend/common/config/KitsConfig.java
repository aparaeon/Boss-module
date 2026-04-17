package gg.mmorealms.module.kits.backend.common.config;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.kits.backend.common.dto.Kit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class KitsConfig {

	public MessageBuilder baseKitPermission = new MessageBuilder("mmorealms.kit.{name}");

	public List<Kit> kits = List.of(
			new Kit(
					"Food",
					7,
					Time.minutes(30),
					List.of(new ItemStack(Items.COOKED_BEEF, 32)),
					new ItemStack(Items.COOKED_BEEF, 1)
			)
	);

	public PreviewGUI previewGUI = new PreviewGUI();
	public KitGUI kitGUI = new KitGUI();

	public Lang lang = new Lang();

	public static class PreviewGUI {
		public GUIButton background = new GUIButton()
				.display(Items.WHITE_STAINED_GLASS_PANE)
				.position(0, 0, 9, 5);

		public GUIButton back = new GUIButton()
				.display(Items.RED_WOOL)
				.displayName("<red>Go back")
				.position(4, 0, 1, 1);

		public GUIButton claim = new GUIButton()
				.display(Items.GREEN_WOOL)
				.displayName("<green>Claim this kit")
				.position(4, 8, 1, 1);

		public GUIButton lore = new GUIButton()
				.display(Items.PAPER)
				.position(4, 4, 1, 1);
	}

	public static class KitGUI {
		public GUIButton background = new GUIButton()
				.display(Items.WHITE_STAINED_GLASS_PANE)
				.position(0, 0, 9, 6);
	}

	public static class Lang {
		public String kitGUITitle = "<black>Kits";

		public MessageBuilder onCooldown = new MessageBuilder("<yellow>Cooldown: {time}");
		public MessageBuilder offCooldown = new MessageBuilder("<dark_green>Cooldown: {time}");

		public MessageBuilder notFoundKit = new MessageBuilder("<red>There is no kit with name {name}");
		public String oneTimeKit = "<yellow>One Time Kit";
		public String redeemedOneTimeKit = "<red>You already redeemed this one time kit!";
		public String redeemableKit = "<green>You can redeem this kit!";
		public MessageBuilder onCooldownKit = new MessageBuilder("<red>You have a cooldown on redeeming this kit");
		public String noPermissionKit = "<red>You don't have the required permission to redeem this kit!";
		public MessageBuilder noSpaceKit = new MessageBuilder("<red>You need {space} free space to be able to claim this kit!");

		public MessageBuilder cooldownMessage = new MessageBuilder("You have to wait {time} to claim {name} kit!");
		public MessageBuilder receivedMessage = new MessageBuilder("Successfully received {name} kit!");
		public String successDeleteMessage = "Successfully deleted kit!";
		public MessageBuilder invalidFormatMessage = new MessageBuilder("Invalid format for {arg}!");

		public MessageBuilder creatingKit = new MessageBuilder("<black>Creating kit {name}");

		public MessageBuilder successCreateMessage = new MessageBuilder("Successfully created {name} kit!");
		public MessageBuilder failedCreateMessage = new MessageBuilder("There already exists a {name} kit!");

		public MessageBuilder successDeleteOptionMessage = new MessageBuilder("Successfully deleted '{option}' from {name} kit!");
		public MessageBuilder failedDeleteOptionMessage = new MessageBuilder("Can't delete {index} from {optionType} from {name} kit as it is out of bounds!");

		public MessageBuilder successAddOptionMessage = new MessageBuilder("Successfully added '{option}' <reset>to kit {name}");
		public String addCommandWarning = "<gold><bold>WARNING: <newline>" + "<reset><red>The command will be executed by the server so be sure to have some kind of placeholder " + "for username '{user}' or UUID '{uuid}' in the command. <newline>";

		public MessageBuilder indexedList = new MessageBuilder("{index}. {field}<newline>");
		public MessageBuilder detailedDescription = new MessageBuilder("Kit {name}<newline>" + "Cooldown: {cooldown}<newline>" + "Commands:<newline>{commands}" + "Lore:<newline>{lore}");
	}
}
