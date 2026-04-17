package gg.mmorealms.module.realms.backend.common.config;

import com.google.gson.JsonObject;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.backend.common.utils.CodecUtils;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.utils.ItemBuilder;
import gg.mmorealms.module.core.common.dto.PagedMessageGUIConfig;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.realms.backend.common.RealmsBackendModule;
import gg.mmorealms.module.realms.backend.common.dto.RealmPermission;
import gg.mmorealms.module.realms.backend.common.dto.RealmType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealmsConfig {
	public MessageBuilder permission = new MessageBuilder("mmorealm.realm.{permission}");
	public Price realmExpansionPrice = new Price(50, CurrencyType.GEMS);

	public Time checkBorder = Time.seconds(5);

	public int maxPagesForVisitorMessage = 1;
	public int maxLinesForVisitorMessage = 14;

	public List<String> teleportBackDenyList = List.of(
			"minecraft:water",
			"minecraft:lava",
			"minecraft:stone"
	);

	public HashMap<RealmType, RealmType.Properties> realmTypeProperties = new HashMap<>() {
		{
			put(RealmType.CHERRY, new RealmType.Properties(
					"Cherry",
					Location.builder(512, 69, 512).build(),
					"cherry",
					"\uF200"
			));
			put(RealmType.FOREST, new RealmType.Properties(
					"Forest",
					Location.builder(512, 69, 512).build(),
					"forest",
					"\uF201"
			));
			put(RealmType.PLAINS, new RealmType.Properties(
					"Plains",
					Location.builder(512, 64, 512).build(),
					"plains",
					"\uF202"
			));
			put(RealmType.SAVANNA, new RealmType.Properties(
					"Savanna",
					Location.builder(512, 64, 512).build(),
					"savanna",
					"\uF203"
			));
		}
	};

	public JsonObject visitorMessageItem = CodecUtils.serialize(ItemStack.CODEC, ItemBuilder.of()
			.display(Items.WRITABLE_BOOK)
			.dataComponent(DataComponents.CUSTOM_NAME, RealmsBackendModule.instance().getMiniMessageManager().parse("<gold>Visitor Message"))
			.dataComponent(DataComponents.MAX_STACK_SIZE, 99)
			.build());

	public HashMap<Integer, Integer> levelToWorldBorder = new HashMap<>() {{
		put(1, 100);
		put(2, 200);
		put(3, 300);
		put(4, 400);
		put(5, 500);
		put(6, 600);
		put(7, 700);
		put(8, 800);
		put(9, 900);
	}};

	public RealmGUI realmGUI = new RealmGUI();
	public PagedGUI pagedGUI = new PagedGUI();
	public RealmCreateGUI realmCreateGUI = new RealmCreateGUI();
	public RealmSettingsGUI realmSettingsGUI = new RealmSettingsGUI();
	public RealmVisitGUI realmVisitGUI = new RealmVisitGUI();
	public RealmMembersGUI realmMembersGUI = new RealmMembersGUI();

	public Map<RealmPermission, String> permissionMap = new HashMap<>() {{
		put(RealmPermission.EVERY_MEMBER, "<red>You are not a member of this realm");

		put(RealmPermission.COMMAND_CHANGE_VISITOR_MESSAGE, "<red>You don't have permission to change the visitor message!");
		put(RealmPermission.COMMAND_BAN, "<red>You don't have permission to ban players on this realm!");
		put(RealmPermission.COMMAND_UNBAN, "<red>You don't have permission to unban players on this realm!");
		put(RealmPermission.COMMAND_TRUST, "<red>You can't trust new players on this realm!");
		put(RealmPermission.COMMAND_ADD, "<red>You can't add members to this realm!");
		put(RealmPermission.COMMAND_REMOVE, "<red>You can't remove members from this realm!");
		put(RealmPermission.COMMAND_MEMBERS, "<red>You can't view the list of members on this realm!");
		put(RealmPermission.COMMAND_DELETE, "<red>You can't delete this realm!");

		put(RealmPermission.BLOCK_ATTACK, "<red>You can't attack blocks in this realm!");
		put(RealmPermission.BLOCK_INTERACT, "<red>You can't interact with blocks in this realm!");
		put(RealmPermission.BLOCK_BREAK, "<red>You can't break blocks in this realm!");
		put(RealmPermission.BLOCK_PLACE, "<red>You can't place blocks in this realm!");
		put(RealmPermission.BOAT_PLACE, "<red>You can't place boats in this realm!");
		put(RealmPermission.DISPENSABLE_BLOCK_PLACE, "<red>You can't place dispensable blocks on this realm!");

		put(RealmPermission.ENTITY_INTERACT, "");
		put(RealmPermission.ENTITY_ATTACK, "<red>You can't attack entities in this realm!");
		put(RealmPermission.ENTITY_DAMAGE, "<red>You can't damage entities in this realm!");

		put(RealmPermission.ARMOR_STAND_INTERACT_EVENT, "<red>You can't interact with armor stands in this realm!");
		put(RealmPermission.DECORATED_POT_INTERACT_EVENT, "<red>You can't interact with the decorated pots in this realm!");
		put(RealmPermission.SIGN_INTERACT_EVENT, "<red>You can't interact with the signs in this realm!");
		put(RealmPermission.ITEM_USE_EVENT, "<red>You can't use items in this realm!");
		put(RealmPermission.ITEM_DROP_EVENT, "<red>You can't drop items in this realm!");

		put(RealmPermission.CROP_STOMP, "<red>You don't have permission to stomp the crops on this realm!");

		put(RealmPermission.SET_HOME, "You don't have permission to set a home on this realm!");
	}};

	public Lang lang = new Lang();

	public PagedMessageGUIConfig helpPageConfig = PagedMessageGUIConfig.builder("Realm help").entriesPerPage(9).numberedList(false).build();

	public static class PagedGUI {
		public List<Integer> slots = List.of(
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24, 25,
				28, 29, 30, 31, 32, 33, 34
		);

		public GUIButton previous = GUIButton.empty()
				.displayName("<yellow>Previous Page")
				.position(5, 1, 2, 1);

		public GUIButton next = GUIButton.empty()
				.displayName("<yellow>Next Page")
				.position(5, 6, 2, 1);
	}

	public static class Lang {
		public String userNotFound = "<red>User not found";

		public MessageBuilder realmOnCrashingServer = new MessageBuilder("<yellow>{pre} realm was previously loaded on a server that crashed. Please wait a moment until we fully save it and rejoin the server again after.");
		public MessageBuilder realmUnloading = new MessageBuilder("<yellow>{pre} realm is getting saved. Please wait a moment...");

		public MessageBuilder realmJoinMessage = new MessageBuilder("<newline><hover:show_text:'<light_purple><bold>Click to teleport into the wild!</bold></light_purple>'><click:run_command:'/rtp'><green><bold>Welcome to {possesive} Realm — builds here are always saved!</bold></green><newline><newline><yellow><bold>{message}<newline><newline><light_purple><bold>──➤ {message2}<newline>\n");

		public MessageBuilder loadingRealmStart = new MessageBuilder("\n\n\n<green>We are loading {pre} realm on server {server}<green>. Please stand by...\n\n\n");

		public String playerHasNoRealm = "<red>The player does not have a realm";
		public String realmDeleted = "<green>Realm deleted";
		public String notInARealm = "<red>You are not in a realm";

		public MessageBuilder invalidParameter = new MessageBuilder("<red>Invalid {parameter}!");

		public MessageBuilder realmMemberEntry = new MessageBuilder("- <gray>{username} - {permission}");
		public String playerHasNoOwnRealm = "<red>You don't have a realm! <white>You can create one with the command /realm";
		public String alreadyInRealm = "<red>Player is already a member of this realm!";
		public MessageBuilder realmStillLoading = new MessageBuilder("<yellow>The realm is still loading. Please wait a moment...");
		public String realmAlreadyLoaded = "<green>Realm already loaded!";
		public String playerHasOwnRealmLoaded = "<green>Realm already loaded, use [/realm] to get to it!";
		public String realmAlreadyUnloaded = "<green>Realm already unloaded!";
		public String realmUnloadStart = "<green>The realm started to unload<newline><yellow>Warning:<white> you will not receive a confirmation message after it is finished";

		public String ownRealmVisit = "<red>You can't visit your own realm!";

		public String realmLoadFailed = "<red>Failed to load your realm. Please contact an administrator for assistance";
		public String adminLoadRealmReminder = "<red>Realm not loaded, please use [/realm admin load <player>] first!";

		public MessageBuilder grantedPermission = new MessageBuilder("<green>Granted <white>{target} {permission}<green> permission in the realm!");
		public MessageBuilder receivedPermission = new MessageBuilder("<green>Your permission level in the realm owned by <white>{owner}<green> is now {permission}!");
		public MessageBuilder memberAddedToRealm = new MessageBuilder("<green>Added <white>{target}<green> to this realm!");
		public MessageBuilder addedToRealm = new MessageBuilder("<green>You were added to a realm owned by <white>{owner}<green>!");
		public MessageBuilder targetNotMember = new MessageBuilder("<red>Player {target} is not a member of this realm!");
		public MessageBuilder memberRemovedFromRealm = new MessageBuilder("<green>Removed <white>{target} <green>from this realm!");
		public MessageBuilder removedFromRealm = new MessageBuilder("<green>You were removed from a realm owned by <white>{owner}<green>!");
		public String legendaryCaptureShared = "<green>Now members of your realm can capture legendary spawned for you!";
		public String legendaryCaptureOwnerOnly = "<green>Now only you can capture legendary spawned for you!";

		public String setSpawnMessage = "<green>You successfully changed the spawn to this realm!";

		public String invalidBan = "<red>You can only ban players with a lower rank than you.";
		public MessageBuilder playerBanned = new MessageBuilder("<green>Successfully banned player {user}");
		public MessageBuilder bannedNotice = new MessageBuilder("<red>You were banned from a realm owned by {owner}");

		public MessageBuilder playerUnbanned = new MessageBuilder("<green>Successfully unbanned player {user}");
		public MessageBuilder unbannedNotice = new MessageBuilder("<red>You were unbanned from a realm owned by {owner}");

		public String realmOwnerLeaveError = "<red>You are the owner of this realm. Use [/realm delete] if you want to delete it!";
		public String realmOwnerNotOnlineLeaveError = "<red>Please try again after we load the realm!";

		public MessageBuilder notRealmMember = new MessageBuilder("<red>You are not a member of a realm owned by {owner}!");

		public String invalidRemove = "<red>You can only remove players with a lower rank than yours.";
		public String invalidTrust = "<red>You can only change the rank of players with a lower trust level.";
		public String invalidTrustLimit = "<red>You may only promote players to a rank just below your own.";

		public String howDoYouGetInRealmSettingsWithoutARealm = "<bold><red>How did you get in this menu if you don't have a realm? Please contact an administrator.";

		public MessageBuilder realmLoaded = new MessageBuilder("""
				
				
				<green>{pre} realm has been loaded onto server {server_id}<green>. You can now go to the realm
				
				
				
				""");

		public String helpMessage = """
				<bold><aqua>Realm Help</aqua></bold>
				<yellow>Your <green>Realm<yellow> is your personal world — build your base, invite others, and make it your own!
				
				<bold><aqua>World Selection</aqua></bold>
				<hover:show_text:'<gray>Open the realm menu</gray>'><click:run_command:/realm><gold>/realm</gold></click></hover> <gray>– Open the Realm menu to manage and view details about your personal realm</gray>
				<hover:show_text:'<gray>Teleport to your personal realm</gray>'><click:run_command:/realm teleport><gold>/realm teleport</gold></click></hover> <gray>– Jump straight to your personal realm</gray>
				<hover:show_text:'<gray>Visit another player’s realm (if public or permitted)</gray>'><click:suggest_command:/realm visit ><gold>/realm visit <player></gold></click></hover> <gray>– Visit someone else’s realm</gray>
				<hover:show_text:'<gray>Click to pick your destination</gray>'><click:run_command:/select><gold>/select</gold></click></hover> <gray>– Opens the server select menu. You’ll also get a compass — just right-click with it!</gray>
				
				<bold><aqua>Realm Management</aqua></bold>
				<red><italic><bold>Note:</bold><yellow> All commands below act on the realm you're currently in, not the one you own.</italic>
				<hover:show_text:'<gray>No going back — this will erase this realm</gray>'><click:run_command:/realm delete><gold>/realm delete</gold></click></hover> <gray>– Permanently delete this realm</gray>
				<hover:show_text:'<gray>Set the spawn point for this realm</gray>'><click:run_command:/realm set_spawn><gold>/realm set_spawn</gold></click></hover> <gray>– Update the spawn location for this realm</gray>
				<hover:show_text:'<gray>See who’s part of this realm</gray>'><click:run_command:/realm members><gold>/realm members</gold></click></hover> <gray>– View members of this realm</gray>
				<hover:show_text:'<gray>Promote or demote a player’s rank in this realm</gray>'><click:suggest_command:/realm trust ><gold>/realm trust <player> <rank></gold></click></hover> <gray>– Change a player’s permission level in this realm</gray>
				<hover:show_text:'<gray>Invite a friend to this realm</gray>'><click:suggest_command:/realm invite ><gold>/realm invite <player></gold></click></hover> <gray>– Invite a player to this realm</gray>
				<hover:show_text:'<gray>Remove a player’s access to this realm</gray>'><click:suggest_command:/realm remove ><gold>/realm remove <player></gold></click></hover> <gray>– Remove a member from this realm</gray>
				<hover:show_text:'<gray>Ban a player from this realm</gray>'><click:suggest_command:/realm ban ><gold>/realm ban <player></gold></click></hover> <gray>– Ban someone from entering this realm</gray>
				<hover:show_text:'<gray>Unban a previously banned player</gray>'><click:suggest_command:/realm unban ><gold>/realm unban <player></gold></click></hover> <gray>– Allow a banned player back into this realm</gray>
				
				<bold><aqua>Ranks & Permissions</aqua></bold>
				<gold><bold>Manager</bold><gray>– Can use realm management commands (except <gold>/realm delete</gold>)
				<yellow><bold>Officer</bold><gray> – Can interact with blocks and containers (e.g., build, use chests)
				<aqua><bold>Member</bold><gray> – Can interact with entities (e.g., pokemons, animals)
				<bold>Visitor</bold><gray> – Can enter the realm even when you're offline
				""";

		public String adminHelpMessage = """
				<bold><aqua>Realm Admin Commands</aqua></bold>
				<hover:show_text:'<gray>Permanently delete a player’s realm</gray>'><click:suggest_command:/realm admin delete ><gold>/realm admin delete <user></gold></click></hover> <gray>– Delete the specified user’s realm</gray>
				<hover:show_text:'<gray>Teleport to the center of a player’s realm</gray>'><click:suggest_command:/realm admin teleport ><gold>/realm admin teleport <user></gold></click></hover> <gray>– Jump to a user’s realm</gray>
				<hover:show_text:'<gray>Force-load a player’s realm if not already loaded</gray>'><click:suggest_command:/realm admin load ><gold>/realm admin load <player></gold></click></hover> <gray>– Load a player’s realm</gray>
				<hover:show_text:'<gray>Change the build border size of a player’s realm</gray>'><click:suggest_command:/realm admin border ><gold>/realm admin border <user> <level></gold></click></hover> <gray>– Set a user’s realm border level</gray>
				
				<bold><aqua>Developer / Debug Tools</aqua></bold>
				<hover:show_text:'<gray>Get your internal realm position offset</gray>'><click:run_command:/realm get_offset><gold>/realm get_offset</gold></click></hover> <gray>– Not typically needed by players</gray>
				<hover:show_text:'<gray>Get your region-level offset in the realm</gray>'><click:run_command:/realm get_region_offset><gold>/realm get_region_offset</gold></click></hover> <gray>– Mostly useful for developers or debugging</gray>
				""";
	}


	public static class RealmSettingsGUI {
		public String noPermissionWeather = "<red>You are not allowed to change the weather of the realm!";
		public String noPermissionTime = "<red>You are not allowed to change the time of the realm!";
		public String noPermissionPokemonSpawn = "<red>You are not allowed to change the pokemon spawning option of this realm!";

		public GUIButton privateLeftButton = new GUIButton()
				.display(ItemBuilder.of()
						.display(Items.PAPER)
						.dataComponent(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1002))
				)
				.displayName("<red>Private")
				.position(38);

		public GUIButton privateRightButton = GUIButton.empty()
				.displayName("<red>Private")
				.position(39);

		public GUIButton publicLeftButton = GUIButton.empty()
				.displayName("<green>Public")
				.position(38);

		public GUIButton publicRightButton = new GUIButton()
				.display(ItemBuilder.of()
						.display(Items.PAPER)
						.dataComponent(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1001))
				)
				.displayName("<green>Public")
				.position(39);

		public GUIButton onTimeLeftButton = new GUIButton()
				.display(ItemBuilder.of()
						.display(Items.PAPER)
						.dataComponent(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1003))
				)
				.displayName("<green>Day / night cycle ON")
				.position(23);

		public GUIButton onTimeRightButton = GUIButton.empty()
				.displayName("<green>Day / night cycle ON")
				.position(24);

		public GUIButton offTimeButton = GUIButton.empty()
				.displayName("<red>Day / night cycle OFF")
				.position(2, 5, 2, 1);

		public GUIButton onWeatherLeftButton = GUIButton.empty()
				.display(ItemBuilder.of()
						.display(Items.PAPER)
						.dataComponent(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1004))
				)
				.displayName("<aqua>Raining")
				.position(20);

		public GUIButton onWeatherRightButton = GUIButton.empty()
				.displayName("<aqua>Raining")
				.position(21);

		public GUIButton offWeatherButton = GUIButton.empty()
				.displayName("<green>Normal Weather")
				.position(2, 2, 2, 1);

		public GUIButton onPokemonSpawningLeftButton = new GUIButton()
				.display(ItemBuilder.of()
						.display(Items.PAPER)
						.dataComponent(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1005))
				)
				.displayName("<green>Pokemon Spawning ON")
				.position(5);

		public GUIButton onPokemonSpawningRightButton = GUIButton.empty()
				.displayName("<green>Pokemon Spawning ON")
				.position(6);

		public GUIButton offPokemonSpawningButton = GUIButton.empty()
				.displayName("<red>Pokemon Spawning OFF")
				.position(0, 5, 2, 1);
	}

	public static class RealmVisitGUI {
		public GUIButton publicFilter = GUIButton.empty()
				.displayName("<green>Public Realms")
				.position(49);

		public GUIButton privateFilter = GUIButton.empty()
				.displayName("<yellow>Private Realms")
				.position(49);

		public GUIButton privateRealm = new GUIButton()
				.displayName("{user}")
				.display(Items.PLAYER_HEAD)
				.skullOwner("{user}")
				.lore(List.of("<green>Left-Click to teleport to this realm",
						"<red>Right-Click to stop being a member of this realm"
				));

		public GUIButton publicRealm = new GUIButton()
				.displayName("{user}")
				.display(Items.PLAYER_HEAD)
				.skullOwner("{user}")
				.lore(List.of("<green>Left-click to teleport to the realm owner by <gold>{user}"));
	}

	public static class RealmMembersGUI {
		public GUIButton members = GUIButton.empty()
				.displayName("<green>Members")
				.position(49);
	}

	public static class RealmCreateGUI {
		public String titleBase = "\uF812";

		public GUIButton previous = GUIButton.empty()
				.displayName("<yellow>Previous Realm Type")
				.position(2, 0, 2, 2);

		public GUIButton next = GUIButton.empty()
				.displayName("<yellow>Next Realm Type")
				.position(2, 7, 2, 2);

		public GUIButton create = GUIButton.empty()
				.displayName("<green>Create Realm")
				.position(5, 3, 3, 1);
	}

	public static class RealmGUI {
		public GUIButton teleport = GUIButton.empty()
				.displayName("<green>Teleport")
				.lore(List.of(
						"",
						"<gray>Teleport to your realm"
				))
				.position(4, 3, 3, 1);

		public GUIButton members = GUIButton.empty()
				.displayName("<white>Members")
				.lore(List.of(
						"",
						"<gray>View all members of this realm"
				))
				.position(4, 0, 2, 2);

		public GUIButton settings = GUIButton.empty()
				.displayName("<white>Settings")
				.lore(List.of(
						"",
						"<gray>View all settings of this realm",
						"<red>This feature is under development"
				))
				.position(4, 7, 2, 2);
	}

}
