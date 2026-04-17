package gg.mmorealms.module.store.backend.common.files;

import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.dto.StoreCurrency;
import gg.mmorealms.module.store.backend.common.dto.StoreKey;
import gg.mmorealms.module.store.backend.common.dto.StoreRank;

import java.util.List;

public class StoreConfig {

	public List<StoreRank> ranks = List.of(
			new StoreRank("ranks_expert", "<#DF3E23><b>Expert", "\uF241", new Price(1350, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add expert"
					)),
					List.of(
							"8x Rare Candies",
							"1x Random Shiny Pokémon",
							"$5,000 PokeCoins",			
							"1x Random Evolution Item",
							"1x Random Pokémon Plushy"
					),
					List.of(
							"2x /sethome",
							"/hat",
							"/nick"
					),
					List.of(
							"/kit expert (4x Poké Balls, 4x Great Balls, 2x Rare Candies — Daily)"
					)
			),
			new StoreRank("ranks_master", "<#9CDB43><b>Master", "\uF242", new Price(3390, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add master"
					)),
					List.of(
							"16x Rare Candies",
							"1x Master Ball",
							"2x Random Shiny Pokémon",
							"$12,500 PokeCoins",			
							"1x Random Evolution Item & Stone",						
							"2x Random Pokémon Plushy"
					),
					List.of(
							"4x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)"
					),
					List.of(
							"All previous daily rank kits.",
							"/kit master (5x Poké Balls, 5x Great Balls, 3x Rare Candies — Daily)"
					)
			),
			new StoreRank("ranks_heroic", "<#02D8E9><b>Heroic", "\uF243", new Price(6900, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add heroic"
					)),
					List.of(
							"24x Rare Candies",
							"2x Master Balls",
							"3x Random Shiny Pokémon",
							"$22,500 PokeCoins",			
							"1x Random Evolution Item & Stone",
							"1x Random Held Item",						
							"2x Random Pokémon Plushy",
							"1x Random Shiny Pokémon Plushy"						
					),
					List.of(
							"6x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash"
					),
					List.of(
							"All previous daily rank kits.",
							"/kit heroic (8x Poké Balls, 8x Great Balls, 4x Rare Candies — Daily)"
					)
			),
			new StoreRank("ranks_mythical", "<#FFDF00><b>Mythical", "\uF244", new Price(10400, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add mythical"
					)),
					List.of(
							"36x Rare Candies",
							"4x Master Balls",
							"4x Random Shiny Pokémon",
							"$35,000 PokeCoins",			
							"1x Random Evolution Item & Stone",
							"1x Random Held Item",	
							"1x EXP Share & Lucky Egg",						
							"2x Random Pokémon Plushy",
							"2x Random Shiny Pokémon Plushy"
					),
					List.of(
							"8x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash",
							"/pokecolor",
							"/nightvision"
					),
					List.of(
							"All previous daily rank kits.",
							"/kit mythical (8x Great Balls, 12x Ultra Balls, 8x Quick Balls, 6x Rare Candies — Daily)"
					)
			),
			new StoreRank("ranks_divine", "<#E6A9EC><b>Divine", "\uF245", new Price(13900, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add divine"
					)),
					List.of(
							"48x Rare Candies",
							"6x Master Balls",
							"5x Random Shiny Pokémon",
							"$50,000 PokeCoins",			
							"1x Random Evolution Item & Stone",
							"2x Random Held Item",
							"1x Random Mint",						
							"1x EXP Share & Lucky Egg",							
							"2x Random Pokémon Plushy",
							"3x Random Shiny Pokémon Plushy"	
					),
					List.of(
							"10x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash",
							"/pokecolor",
							"/nightvision",
							"/enderchest",
							"/fly"
					),
					List.of(
							"All previous daily rank kits.",
							"/kit divine (16x Great Balls, 16x Ultra Balls, 12x Quick Balls, 6x Rare Candies — Daily)"
					)
			),
			new StoreRank("ranks_arbiter", "<#FFDF00><b>Arbiter", "\uF246", new Price(17400, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add arbiter"
					)),
					List.of(
							"64x Rare Candies",
							"8x Master Balls",
							"6x Random Shiny Pokémon",
							"1x Random Ultra Beast",
							"$70,000 PokeCoins",			
							"1x Random Evolution Item & Stone",
							"2x Random Held Items & Random Mints",					
							"1x EXP Share & Lucky Egg",							
							"2x Random Pokémon Plushy",
							"4x Random Shiny Pokémon Plushy"	
					),
					List.of(
							"12x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash",
							"/pokecolor",
							"/nightvision",
							"/enderchest",
							"/fly",
							"/enchant",
							"/pokesee",
							"/anvil"
					),
					List.of(
							"All previous daily rank kits.",
							"/kit arbiter (16x Great Balls, 20x Ultra Balls, 16x Level Balls, 10x Rare Candies — Daily)",
							"/kit arbitermonthly (1x Basic Key — Every 4 Weeks)"	
					)
			),
			new StoreRank("ranks_champion", "<#2B60DE><b>Champion", "\uF247", new Price(20900, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add champion"
					)),
					List.of(
							"80x Rare Candies",
							"11x Master Balls",
							"6x Random Shiny Pokémon",
							"1x Random Ultra Beast & Shiny Ultra Beast",
							"$95,000 PokeCoins",		
							"1x Random Evolution Item & Stone",
							"2x Random Held Items & Random Mints",						
							"1x EXP Share & Lucky Egg",							
							"2x Random Pokémon Plushy",
							"4x Random Shiny Pokémon Plushy",
							"2x Random Ultra Beast Plushy"				
					),
					List.of(
							"14x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash",
							"/pokecolor",
							"/nightvision",
							"/enderchest",
							"/fly",
							"/enchant",
							"/pokesee",
							"/anvil",
							"/repair",
							"/anvil"
					),
					List.of(
							"All previous daily and monthly rank kits.",
							"/kit champion (16x Great Balls, 16x Ultra Balls, 16x Level Balls, 10x Rare Candies — Daily)",
							"/kit championmonthly (1x Basic Key — Every 4 Weeks)"	
					)
			),
			new StoreRank("ranks_grand_master", "<#840000><b>GrandMaster", "\uF248", new Price(27900, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add grandmaster"
					)),
					List.of(
							"104x Rare Candies",
							"14x Master Balls",
							"5x Random Shiny Pokémon",
							"1x Random Ultra Beast, Shiny Ultra Beast & Legendary",
							"$130,000 PokeCoins",			
							"1x Random Evolution Item & Stone",
							"2x Random Held Items & Random Mints",							
							"1x EXP Share & Lucky Egg",						
							"2x Random Pokémon Plushy",
							"4x Random Shiny Pokémon Plushy",
							"1x Random Legendary & Ultra Beast Plushies"				
					),
					List.of(
							"16x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash",
							"/pokecolor",
							"/nightvision",
							"/enderchest",
							"/fly",
							"/enchant",
							"/pokesee",
							"/anvil",
							"/repair",
							"/anvil",
							"/breed (60 min cooldown)",
							"/stonecutter"
					),
					List.of(
							"All previous daily and monthly rank kits.",
							"/kit grandmaster (24x Great Balls, 24x Ultra Balls, 24x Level Balls, 12x Rare Candies — Daily)",
							"/kit grandmastermonthly (1x Premium Key — Every 4 Weeks)"	
					)
			),
			new StoreRank("ranks_celestial", "<#AA23FF><b>Celestial", "\uF249", new Price(34900, CurrencyType.GEMS),
					new MessageBuilderList(List.of(
							"lp user {user} parent add celestial"
					)),
					List.of(
							"128x Rare Candies",
							"16x Master Balls",
							"5x Random Shiny Pokémon",
							"1x Random Ultra Beast, Shiny Ultra Beast, Legendary & Shiny Legendary",
							"$180,000 PokeCoins",			
							"1x Random Evolution Item & Stone",
							"2x Random Held Items & Random Mints",					
							"1x EXP Share & Lucky Egg",					
							"2x Random Pokémon Plushy",
							"4x Random Shiny Pokémon Plushy",
							"1x Random Ultra Beast, Legendary & Ultra Beast Plushies"						
					),
					List.of(
							"20x /sethome",
							"/hat",
							"/nick",
							"/craft",
							"/pokeheal (5 min cooldown)",
							"/pc",
							"/trash",
							"/pokecolor",
							"/nightvision",
							"/enderchest",
							"/fly",
							"/enchant",
							"/pokesee",
							"/anvil",
							"/repair",
							"/anvil",
							"/breed (60 min cooldown)",
							"/stonecutter",
							"/hatch (60 min cooldown)",
							"/smelt"
					),
					List.of(
							"All previous daily and monthly rank kits.",
							"/kit celestial (32x Quick Balls, 32x Dusk Balls, 14x Rare Candies — Daily)",
							"/kit celestialmonthly (1x Premium Key — Every 4 Weeks)"						
					)
			)
	);

	public List<StoreKey> keys = List.of(
			new StoreKey("keys_basic", "<#00D2CE><b>Basic", "\uF221", new Price(535, CurrencyType.GEMS), new MessageBuilderList(List.of(
					"crates admin give_virtual_key {user} 1 basic"
			))),
			new StoreKey("keys_premium", "<#FC7700><b>Premium", "\uF222", new Price(2740, CurrencyType.GEMS), new MessageBuilderList(List.of(
					"crates admin give_virtual_key {user} 1 premium"
			)))
	);

//	public List<StoreCurrency> currencies = List.of(
//			new StoreCurrency("????x PokeCoins", "FB0000", "\uF211", new Price(1000, CurrencyType.GEMS), new MessageBuilderList(List.of(
//					"balance add {user} 1000 PokeCoins"
//			))),
//			new StoreCurrency("????x Tokens", "FBA800", "\uF212", new Price(1000, CurrencyType.GEMS), new MessageBuilderList(List.of(
//					"balance add {user} 1000 Tokens"
//			)))
//	);

	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder rankEntryTemplate = new MessageBuilder("<gray>- {entry}<reset>");
		public MessageBuilder discountTemplate = new MessageBuilder("<gray><st>{old_price}<reset> <aqua>{price}");

		public MessageBuilderList rankTemplate = new MessageBuilderList(List.of(
				"<white>🎁 <gold>One Time Rewards [<gray>/kit onetime<gold> or <gray>/kit <blue>RANKNAMEHERE<gray>onetime<gold>]<reset>",
				"{one_time_items}",
				"<red><bold>NOTE: You only get the differences between ranks if choosing this as a rank up for one time rewards.",
				"",
				"<white>✨ <gold>Command Perks<reset>",
				"{commands}",
				"",
				"<white>📦 <gold>Kits<reset>",
				"{kits}",
				"",
				"<aqua>Price: {price_tag}<reset>"
		));

		public String storeMessage = "<gold><b>-------------------------------<reset><newline><click:open_url:https://store.mmorealms.gg><aqua><b>Click here to open the store<newline>Or visit https://store.mmorealms.gg</click><newline><gold><b>-------------------------------";

		public MessageBuilderList keyTemplate = new MessageBuilderList(List.of(
				"",
				"<gray>You can use this key at <aqua>/warp crates<gray>.",
				"",
				"<aqua>Price: {price_tag}<reset>"
		));

		public MessageBuilderList currencyTemplate = new MessageBuilderList(List.of(
				"",
				"<aqua>Price: {price_tag}<reset>"
		));
	}

	public void bake() {
		this.ranks.forEach(StoreRank::bake);
		this.keys.forEach(StoreKey::bake);
//		this.currencies.forEach(StoreCurrency::bake);
	}

}
