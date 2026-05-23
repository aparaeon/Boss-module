package gg.mmorealms.module.essentials.backend.common.config;

import com.google.gson.JsonElement;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import gg.mmorealms.module.core.common.dto.PagedMessageGUIConfig;
import gg.mmorealms.module.essentials.backend.common.EssentialsBackendModule;
import gg.mmorealms.module.essentials.backend.common.dto.ItemGroup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;

public class EssentialsConfig {
	public int borderSize = 20000;
	public Location spawnLocation = Location.builder(0.5, 60, 0.5)
			.pitch(0)
			.yaw(-90)
			.build();

	public Time repairCooldown = Time.minutes(5);
	public Time smeltCooldown = Time.minutes(5);

	public JsonElement selectCompass = CodecUtils.serialize(
			ItemStack.CODEC,
			ItemBuilder.of()
					.display(Items.COMPASS)
					.dataComponent(DataComponents.CUSTOM_NAME, EssentialsBackendModule.instance().getMiniMessageManager().parse("<aqua>Realm selector"))
					.dataComponent(DataComponents.MAX_STACK_SIZE, 99)
					.build()
	);

	public SelectGUI selectGUI = new SelectGUI();

	public static class SelectGUI {
		public GUIButton teleportSpawn = GUIButton.empty()
				.name("Teleport to Spawn")
				.position(4, 0, 3, 1);

		public GUIButton teleportRealm = GUIButton.empty()
				.name("Teleport to Realm")
				.position(4, 3, 3, 1);

		public GUIButton teleportWild = GUIButton.empty()
				.name("Teleport to Wild")
				.position(4, 6, 3, 1);
	}

	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder firstJoinMessage = new MessageBuilder("<grey>Welcome <gold>{name}<grey> to <bold><gradient:red:aqua>Cobblemon MMO<reset>!");
	}

	public HashMap<String, ItemGroup> itemGroups = new HashMap<>() {{
		put("useless", new ItemGroup(
				List.of(
						new ItemGroup.Item("minecraft:dirt", Range.of(16, 32)),
						new ItemGroup.Item("minecraft:stone", Range.of(16, 32))
				)));
	}};

	public List<String> rules = List.of(
			"<gold>Keep chat PG-13 - English only in global chat, no roleplaying, no swearing, use common sense and don't disrupt other players.",
			"<gold>Do not advertise, spam, use racial slurs, discuss politics/religion.",
			"<gold>Do not grief/steal/raid builds without permission from the owner.",
			"<gold>Do not use any harmful / objectionable language towards any players or staff.",
			"<gold>Do not use any mods/ resource packs or clients which grant an unfair advantage.",
			"<gold>Do not cheat or exploit server bugs, instead report them on discord.",
			"<gold>Do not scam or over/under price items/pokemon on the server.",
			"<gold>No inappropriate names/skins/text/builds/signs for users/pokemon/items & no nickname impersonations.",
			"<gold>Giveaways are strictly forbidden. (Includes drop-parties, small-giveaways, donations, etc.)",
			"<gold>Giving prizes for player run events is forbidden (Tournaments, Casinos, Wagers, etc)",
			"<gold>Do not beg players or staff for Items/Pokemon/Tokens/Etc.",
			"<gold>Selling/Buying Items/Pokemon/Tokens/Etc with money or gift cards outside the MMO Realms Store is strictly forbidden.",
			"<gold>Do not sell teleport requests or sell shiny/ub/legendaries catches.",
			"<gold>Using ''alts'' or alternative accounts for personal advantage is forbidden.",
			"<gold>Loaning, lending or borrowing of Pokemon is forbidden.",
			"<gold>Do not discuss punishments in chat or report players.7: Do not avoid the chat filter in place by swearing or using any words that's currently blocked.",
			"<gold>Do not use abbreviations which includes words or swears blocked by the censor. If you can't say it unabbreviated then don't say it at all.",
			"<gold>Do not argue with staff in public or private chats, listen to what they say and follow it. If you wish to report a staff member then contact a Head Admin or Manager."
	);

	public PagedMessageGUIConfig rulesPageConfig = PagedMessageGUIConfig.builder("Rules").entriesPerPage(6).build();
	public InvSeeGUI invSeeGUI = new InvSeeGUI();
	public EnderChestSeeGUI enderChestSeeGUI = new EnderChestSeeGUI();

	public static class InvSeeGUI {

		public List<Integer> armorSlots = List.of(0, 1, 2, 3);
		public List<Integer> inventorySlots = List.of(
				9, 10, 11, 12, 13, 14, 15, 16, 17,
				18, 19, 20, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33, 34, 35
		);
		public List<Integer> hotbarSlots = List.of(45, 46, 47, 48, 49, 50, 51, 52, 53);
		public List<Integer> offHandSlots = List.of(8);

		public List<Integer> backgroundSlots = List.of(

		);

		public GUIButton background = GUIButton.of(Items.BLACK_STAINED_GLASS_PANE)
				.name("")
				.position(
						4, 5, 6, 7,
						36, 37, 38, 39, 40, 41, 42, 43, 44
				);

		public GUIButton targetItem = GUIButton.of()
				.lore(
						"",
						"<red>Click to remove"
				);
		public GUIButton userItem = GUIButton.of()
				.lore(
						"",
						"<green>Click to add"
				);

		public int statusIndex = 40;
		public GUIButton onlineStream = GUIButton.of(Items.LIME_WOOL)
				.position(40)
				.name("<green>Online");
		public GUIButton temporaryOfflineStream = GUIButton.of(Items.YELLOW_WOOL)
				.position(40)
				.name("<green>Temporary Offline")
				.lore(
						"",
						"Attempting to retrieve inventory..."
				);
		public GUIButton offlineStream = GUIButton.of(Items.RED_WOOL)
				.name("<red>Offline");

		public int offlineStreamThreshold = 50;

	}

	public static class EnderChestSeeGUI {
		public List<Integer> itemsSlot = List.of(
				0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17,
				18, 19, 20, 21, 22, 23, 24, 25, 26
		);

		public List<Integer> backgroundSlots = List.of(
				27, 28, 29, 30, 32, 33, 34, 35
		);

		public int offlineStreamThreshold = 50;

		public GUIButton targetItem = GUIButton.of()
				.lore(
						"",
						"<red>Click to remove"
				);

		public GUIButton userItem = GUIButton.of()
				.lore(
						"",
						"<green>Click to add"
				);

		public GUIButton onlineStream = GUIButton.of(Items.LIME_WOOL)
				.position(31)
				.name("<green>Online");

		public GUIButton temporaryOfflineStream = GUIButton.of(Items.YELLOW_WOOL)
				.position(31)
				.name("<green>Temporary Offline")
				.lore(
						"",
						"Attempting to retrieve inventory..."
				);

		public GUIButton offlineStream = GUIButton.of(Items.RED_WOOL)
				.position(31)
				.name("<red>Offline");

		public GUIButton backgroundItem = GUIButton.of(Items.GRAY_STAINED_GLASS_PANE)
				.position(
						27, 28, 29, 30, 32, 33, 34, 35
				)
				.name("");
	}
}