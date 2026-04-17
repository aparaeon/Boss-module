package gg.mmorealms.module.shop.backend.common.config;

import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.world.item.Items;

import java.util.List;

public class ShopConfig {

	public ShopGUI shopGUI = new ShopGUI();

	public Lang lang = new Lang();

	public static class ShopGUI {
		public GUI.Settings settings = new GUI.Settings()
				.chestSize(6);

		public int shopItemRows = 5;
		public int shopColumns = 9;
		public int shopBackOffset = 0;
		public int shopBalanceOffset = 8;
		public int shopPreviousPageOffset = 3;
		public int shopCurrentPageOffset = 4;
		public int shopNextPageOffset = 5;
		public MessageBuilder shopTitle = new MessageBuilder("{shopName}");

		public int mainMenuBackOffset = 4;
		public String mainMenuTitle = "Shops";

		public String currentPageItem = CommonMethods.getItemId(Items.PAPER);
		public GUIButton background = new GUIButton()
				.display(Items.WHITE_STAINED_GLASS_PANE)
				.position(0, 0, 9, 6);

		public GUIButton back = new GUIButton()
				.displayName("Back")
				.display(Items.BARRIER);

		public GUIButton balance = new GUIButton()
				.display(Items.PLAYER_HEAD)
				.skullOwner("{user}")
				.displayName("Your balance: <italic><green>{amount} {currency_color}{currency}");

		public GUIButton previousPage = new GUIButton()
				.display(Items.RED_WOOL)
				.displayName("<aqua><italic>Previous page");

		public GUIButton currentPage = new GUIButton()
				.displayName(new MessageBuilder("<aqua><italic>Current page: {currentPage}")
						.parse("page", 1)
						.parse());

		public GUIButton nextPage = new GUIButton()
				.display(Items.GREEN_WOOL)
				.displayName("<aqua><italic>Next page");

		public int getShopSize() {
			return shopItemRows * shopColumns;
		}
	}

	public static class Lang {
		public String errorShopNotFound = "<red>Shop not found!";
		public String errorShopEmpty = "<red>The shop does not contain any items.";

		public String errorNoItemInHand = "<red>You are not holding any item in your main hand.";
		public String errorInvalidSellItem = "<red>The item you are holding cannot be sold.";

		public String errorInvalidBuyItem = "<red>This item cannot be bought in this shop.";
		public String errorInsufficientFunds = "<red>You don't have enough money.";

		public MessageBuilder messageTransactionSuccess = new MessageBuilder("<green>You have {type} <aqua>{amount} <yellow>{item} <green>for <gold>{price} {currency_color}{currency}<reset>.");
		public MessageBuilder logMessageTransactionSuccess = new MessageBuilder("<green>{user} have {type} <aqua>{amount} <yellow>{item} <green>for <gold>{price} {currency_color}{currency}<reset>.");

		public String errorItemNotSellable = "<red>This item cannot be sold in this shop.";
		public String errorPlayerLacksItem = "<red>You don't have this item.";

		public List<Pair2<MessageBuilder, String>> shopItemLore = List.of(
				new Pair2<>(new MessageBuilder("{description}"),
						null
				),
				new Pair2<>(new MessageBuilder("<green>Left click to buy for <yellow>{buyItemPrice}"),
						null
				),
				new Pair2<>(new MessageBuilder("<red>Right click to sell for <yellow>{sellItemPrice}"),
						null
				),
				new Pair2<>(new MessageBuilder("<aqua>Hold shift to trade up to a stack of items"),
						null
				)
		);

		public MessageBuilder listTitle = new MessageBuilder("\n{shop_name} items list:\n\n");
		public MessageBuilder listEntry = new MessageBuilder("Item name: <grey>{item_name}<reset>, Buy price: <green>{buy_price}<reset>," +
				" Sell price: <red>{sell_price}<reset>\n");

		public String messageDeleteSuccess = "<green>Shop successfully removed!";
		public String messageReloadSuccess = "<green>Reloaded config!";
	}

	public List<Shop> shops = List.of(
			Shop.builder()
					.name("Pokeballs")
					.defaultCurrency(CurrencyType.POKECOINS)
					.slot(-1)
					.items(List.of(
							new ShopItem("cobblemon:poke_ball", 20, 1),
							new ShopItem("cobblemon:citrine_ball", 30, 1),
							new ShopItem("cobblemon:verdant_ball", 30, 1),
							new ShopItem("cobblemon:azure_ball", 30, 1),
							new ShopItem("cobblemon:roseate_ball", 30, 1),
							new ShopItem("cobblemon:slate_ball", 30, 1),
							new ShopItem("cobblemon:great_ball", 40, 1),
							new ShopItem("cobblemon:ultra_ball", 50, 1),
							new ShopItem("cobblemon:level_ball", 100, 2),
							new ShopItem("cobblemon:moon_ball", 25, 1),
							new ShopItem("cobblemon:friend_ball", 50, 1),
							new ShopItem("cobblemon:love_ball", 30, 1),
							new ShopItem("cobblemon:safari_ball", 30, 1),
							new ShopItem("cobblemon:heavy_ball", 50, 1),
							new ShopItem("cobblemon:fast_ball", 80, 1),
							new ShopItem("cobblemon:repeat_ball", 100, 2),
							new ShopItem("cobblemon:timer_ball", 100, 2),
							new ShopItem("cobblemon:nest_ball", 25, 1),
							new ShopItem("cobblemon:net_ball", 100, 1),
							new ShopItem("cobblemon:dive_ball", 75, 1),
							new ShopItem("cobblemon:luxury_ball", 35, 1),
							new ShopItem("cobblemon:heal_ball", 25, 1),
							new ShopItem("cobblemon:dusk_ball", 150, 2),
							new ShopItem("cobblemon:quick_ball", 150, 2),
							new ShopItem("cobblemon:premier_ball", 100, 2),
							new ShopItem("cobblemon:sport_ball", 30, 1),
							new ShopItem("cobblemon:lure_ball", 80, 1),
							new ShopItem("cobblemon:dream_ball", 100, 2),
							new ShopItem("cobblemon:park_ball", 100, 2),
							new ShopItem("cobblemon:ancient_poke_ball", 20, 1),
							new ShopItem("cobblemon:ancient_citrine_ball", 30, 1),
							new ShopItem("cobblemon:ancient_verdant_ball", 30, 1),
							new ShopItem("cobblemon:ancient_azure_ball", 30, 1),
							new ShopItem("cobblemon:ancient_roseate_ball", 30, 1),
							new ShopItem("cobblemon:ancient_slate_ball", 30, 1),
							new ShopItem("cobblemon:ancient_great_ball", 40, 1),
							new ShopItem("cobblemon:ancient_ultra_ball", 50, 1),
							new ShopItem("cobblemon:ancient_leaden_ball", 30, 1),
							new ShopItem("cobblemon:ancient_feather_ball", 40, 1),
							new ShopItem("cobblemon:ancient_heavy_ball", 50, 1),
							new ShopItem("cobblemon:ancient_gigaton_ball", 40, 1),
							new ShopItem("cobblemon:ancient_wing_ball", 80, 1),
							new ShopItem("cobblemon:ancient_jet_ball", 120, 2)
					))
					.build()
	);
}
