package gg.mmorealms.module.tms.backend.fabric.config;

import com.cobblemon.mod.common.CobblemonItems;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.common.dto.Price;
import net.minecraft.world.item.Items;

import java.util.*;

public class TMsConfig {

	public Lang lang = new Lang();

	public static class Lang {
		public MessageBuilder cantLearnMoveInBattle = new MessageBuilder("<red>{pokemonName} can't learn moves while in battle");
		public MessageBuilder notEnoughCurrency = new MessageBuilder("<red>Not enough {currencyType}");

		public MessageBuilder pokemonLearnedMove = new MessageBuilder("<green>{pokemonName} successfully learned {moveName}");
	}

	/* ---------- TMs ---------- */

	public TMMovesGUIConfig tmMovesGUIConfig = new TMMovesGUIConfig();

	public static class TMMovesGUIConfig {
		public MessageBuilder title = new MessageBuilder("<b><blue>TMs - {pokemonName} (Page {page})");
		public int guiRows = 6;

		public List<Integer> slots = List.of(
				0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17,
				18, 19, 20, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33, 34, 35,
				36, 37, 38, 39, 40, 41, 42, 43, 44
		);

		public Map<TMElementalType, GUIButton> tmElementToItem = new HashMap<>() {{
			put(TMElementalType.NORMAL, new GUIButton()
					.displayName("<#DDDDCF>{moveName} (Normal)")
					.display(CobblemonItems.NORMAL_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.FIRE, new GUIButton()
					.displayName("<#E55C32>{moveName} (Fire)")
					.display(CobblemonItems.FIRE_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.WATER, new GUIButton()
					.displayName("<#4A9BE8>{moveName} (Water)")
					.display(CobblemonItems.WATER_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.GRASS, new GUIButton()
					.displayName("<#4DBC3C>{moveName} (Grass)")
					.display(CobblemonItems.GRASS_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.ELECTRIC, new GUIButton()
					.displayName("<#EFD128>{moveName} (Electric)")
					.display(CobblemonItems.ELECTRIC_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.ICE, new GUIButton()
					.displayName("<#6BC3EF>{moveName} (Ice)")
					.display(CobblemonItems.ICE_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.FIGHTING, new GUIButton()
					.displayName("<#C44C5C>{moveName} (Fighting)")
					.display(CobblemonItems.FIGHTING_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.POISON, new GUIButton()
					.displayName("<#A24BD8>{moveName} (Poison)")
					.display(CobblemonItems.POISON_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.GROUND, new GUIButton()
					.displayName("<#D89950>{moveName} (Ground)")
					.display(CobblemonItems.GROUND_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.FLYING, new GUIButton()
					.displayName("<#BCC1FF>{moveName} (Flying)")
					.display(CobblemonItems.FLYING_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.PSYCHIC, new GUIButton()
					.displayName("<#D86AD6>{moveName} (Psychic)")
					.display(CobblemonItems.PSYCHIC_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.BUG, new GUIButton()
					.displayName("<#A2C831>{moveName} (Bug)")
					.display(CobblemonItems.BUG_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.ROCK, new GUIButton()
					.displayName("<#AA9666>{moveName} (Rock)")
					.display(CobblemonItems.ROCK_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.GHOST, new GUIButton()
					.displayName("<#9572E5>{moveName} (Ghost)")
					.display(CobblemonItems.GHOST_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.DRAGON, new GUIButton()
					.displayName("<#535DE8>{moveName} (Dragon)")
					.display(CobblemonItems.DRAGON_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.DARK, new GUIButton()
					.displayName("<#5C6CB2>{moveName} (Dark)")
					.display(CobblemonItems.DARK_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.STEEL, new GUIButton()
					.displayName("<#C3CCE0>{moveName} (Steel)")
					.display(CobblemonItems.STEEL_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);

			put(TMElementalType.FAIRY, new GUIButton()
					.displayName("<#EA727E>{moveName} (Fairy)")
					.display(CobblemonItems.FAIRY_GEM)
					.lore(List.of(
							"<yellow>Price: {tmPriceAmount} {tmPriceType}",
							"<yellow>Balance: {currencyAmount} {currencyType}"
					))
			);
		}};

		public GUIButton nextPageItem = new GUIButton()
				.display(Items.ARROW)
				.displayName("<b><white>Next →")
				.position(5, 8);

		public GUIButton previousPageItem = new GUIButton()
				.display(Items.ARROW)
				.displayName("<b><white>← Previous")
				.position(5, 0);

		public List<GUIButton> background = List.of(
				new GUIButton()
						.displayName("")
						.display(Items.CYAN_STAINED_GLASS_PANE)
						.position(0, 0, 9, 5)
				,

				new GUIButton()
						.displayName("")
						.display(Items.BLUE_STAINED_GLASS_PANE)
						.position(5, 0, 9, 1)

		);
	}

	/* ---------- Party ---------- */

	public PokemonPartyGUIConfig pokemonPartyGUIConfig = new PokemonPartyGUIConfig();

	public static class PokemonPartyGUIConfig {
		public String title = "<b><blue>Who to teach move to?";
		public int guiRows = 6;

		public List<Integer> slots = List.of(
				10, 13, 16,
				28, 31, 34
		);

		public List<GUIButton> background = List.of(
				new GUIButton()
						.displayName("")
						.display(Items.CYAN_STAINED_GLASS_PANE)
						.position(0, 0, 9, 5)
				,

				new GUIButton()
						.displayName("")
						.display(Items.BLUE_STAINED_GLASS_PANE)
						.position(5, 0, 9, 1)

		);
	}

	/* ---------- TMs Data ---------- */

	// Sort Tm moves in alphabetical order once
	public transient List<String> sortedTmMoves;

	public void initSortedTmMoves() {
		sortedTmMoves = new ArrayList<>(tmMoves.keySet());
		Collections.sort(sortedTmMoves);
	}

	public Map<String, Price> tmMoves = new HashMap<>() {{
		put("acidspray", new Price(CurrencyType.POKECOINS, 5000));
		put("acrobatics", new Price(CurrencyType.POKECOINS, 5000));
		put("aerialace", new Price(CurrencyType.POKECOINS, 5000));
		put("afteryou", new Price(CurrencyType.POKECOINS, 5000));
		put("agility", new Price(CurrencyType.POKECOINS, 5000));
		put("aircutter", new Price(CurrencyType.POKECOINS, 5000));
		put("airslash", new Price(CurrencyType.POKECOINS, 5000));
		put("alluringvoice", new Price(CurrencyType.POKECOINS, 5000));
		put("allyswitch", new Price(CurrencyType.POKECOINS, 5000));
		put("amnesia", new Price(CurrencyType.POKECOINS, 5000));
		put("ancientpower", new Price(CurrencyType.POKECOINS, 5000));
		put("aquatail", new Price(CurrencyType.POKECOINS, 5000));
		put("assurance", new Price(CurrencyType.POKECOINS, 5000));
		put("attract", new Price(CurrencyType.POKECOINS, 5000));
		put("aurasphere", new Price(CurrencyType.POKECOINS, 5000));
		put("auroraveil", new Price(CurrencyType.POKECOINS, 5000));
		put("avalanche", new Price(CurrencyType.POKECOINS, 5000));
		put("babydolleyes", new Price(CurrencyType.POKECOINS, 5000));
		put("batonpass", new Price(CurrencyType.POKECOINS, 5000));
		put("beatup", new Price(CurrencyType.POKECOINS, 5000));
		put("bind", new Price(CurrencyType.POKECOINS, 5000));
		put("blastburn", new Price(CurrencyType.POKECOINS, 5000));
		put("blazekick", new Price(CurrencyType.POKECOINS, 5000));
		put("blizzard", new Price(CurrencyType.POKECOINS, 5000));
		put("block", new Price(CurrencyType.POKECOINS, 5000));
		put("bodypress", new Price(CurrencyType.POKECOINS, 5000));
		put("bodyslam", new Price(CurrencyType.POKECOINS, 5000));
		put("bounce", new Price(CurrencyType.POKECOINS, 5000));
		put("bravebird", new Price(CurrencyType.POKECOINS, 5000));
		put("breakingswipe", new Price(CurrencyType.POKECOINS, 5000));
		put("brickbreak", new Price(CurrencyType.POKECOINS, 5000));
		put("brine", new Price(CurrencyType.POKECOINS, 5000));
		put("brutalswing", new Price(CurrencyType.POKECOINS, 5000));
		put("bugbite", new Price(CurrencyType.POKECOINS, 5000));
		put("bugbuzz", new Price(CurrencyType.POKECOINS, 5000));
		put("bulkup", new Price(CurrencyType.POKECOINS, 5000));
		put("bulldoze", new Price(CurrencyType.POKECOINS, 5000));
		put("bulletseed", new Price(CurrencyType.POKECOINS, 5000));
		put("burningjealousy", new Price(CurrencyType.POKECOINS, 5000));
		put("calmmind", new Price(CurrencyType.POKECOINS, 5000));
		put("captivate", new Price(CurrencyType.POKECOINS, 5000));
		put("charge", new Price(CurrencyType.POKECOINS, 5000));
		put("chargebeam", new Price(CurrencyType.POKECOINS, 5000));
		put("charm", new Price(CurrencyType.POKECOINS, 5000));
		put("chillingwater", new Price(CurrencyType.POKECOINS, 5000));
		put("closecombat", new Price(CurrencyType.POKECOINS, 5000));
		put("coaching", new Price(CurrencyType.POKECOINS, 5000));
		put("confide", new Price(CurrencyType.POKECOINS, 5000));
		put("confuseray", new Price(CurrencyType.POKECOINS, 5000));
		put("corrosivegas", new Price(CurrencyType.POKECOINS, 5000));
		put("cosmicpower", new Price(CurrencyType.POKECOINS, 5000));
		put("covet", new Price(CurrencyType.POKECOINS, 5000));
		put("crosspoison", new Price(CurrencyType.POKECOINS, 5000));
		put("crunch", new Price(CurrencyType.POKECOINS, 5000));
		put("curse", new Price(CurrencyType.POKECOINS, 5000));
		put("cut", new Price(CurrencyType.POKECOINS, 5000));
		put("darkestlariat", new Price(CurrencyType.POKECOINS, 5000));
		put("darkpulse", new Price(CurrencyType.POKECOINS, 5000));
		put("dazzlinggleam", new Price(CurrencyType.POKECOINS, 5000));
		put("defog", new Price(CurrencyType.POKECOINS, 5000));
		put("dig", new Price(CurrencyType.POKECOINS, 5000));
		put("disarmingvoice", new Price(CurrencyType.POKECOINS, 5000));
		put("dive", new Price(CurrencyType.POKECOINS, 5000));
		put("doubleedge", new Price(CurrencyType.POKECOINS, 5000));
		put("doubleteam", new Price(CurrencyType.POKECOINS, 5000));
		put("dracometeor", new Price(CurrencyType.POKECOINS, 5000));
		put("dragoncheer", new Price(CurrencyType.POKECOINS, 5000));
		put("dragonclaw", new Price(CurrencyType.POKECOINS, 5000));
		put("dragondance", new Price(CurrencyType.POKECOINS, 5000));
		put("dragonpulse", new Price(CurrencyType.POKECOINS, 5000));
		put("dragontail", new Price(CurrencyType.POKECOINS, 5000));
		put("drainingkiss", new Price(CurrencyType.POKECOINS, 5000));
		put("drainpunch", new Price(CurrencyType.POKECOINS, 5000));
		put("dreameater", new Price(CurrencyType.POKECOINS, 5000));
		put("drillrun", new Price(CurrencyType.POKECOINS, 5000));
		put("dualchop", new Price(CurrencyType.POKECOINS, 5000));
		put("dualwingbeat", new Price(CurrencyType.POKECOINS, 5000));
		put("earthpower", new Price(CurrencyType.POKECOINS, 5000));
		put("earthquake", new Price(CurrencyType.POKECOINS, 5000));
		put("echoedvoice", new Price(CurrencyType.POKECOINS, 5000));
		put("eerieimpulse", new Price(CurrencyType.POKECOINS, 5000));
		put("electricterrain", new Price(CurrencyType.POKECOINS, 5000));
		put("electroball", new Price(CurrencyType.POKECOINS, 5000));
		put("electroweb", new Price(CurrencyType.POKECOINS, 5000));
		put("embargo", new Price(CurrencyType.POKECOINS, 5000));
		put("encore", new Price(CurrencyType.POKECOINS, 5000));
		put("endeavor", new Price(CurrencyType.POKECOINS, 5000));
		put("endure", new Price(CurrencyType.POKECOINS, 5000));
		put("energyball", new Price(CurrencyType.POKECOINS, 5000));
		put("expandingforce", new Price(CurrencyType.POKECOINS, 5000));
		put("explosion", new Price(CurrencyType.POKECOINS, 5000));
		put("facade", new Price(CurrencyType.POKECOINS, 5000));
		put("faketears", new Price(CurrencyType.POKECOINS, 5000));
		put("falseswipe", new Price(CurrencyType.POKECOINS, 5000));
		put("featherdance", new Price(CurrencyType.POKECOINS, 5000));
		put("fireblast", new Price(CurrencyType.POKECOINS, 5000));
		put("firefang", new Price(CurrencyType.POKECOINS, 5000));
		put("firepledge", new Price(CurrencyType.POKECOINS, 5000));
		put("firepunch", new Price(CurrencyType.POKECOINS, 5000));
		put("firespin", new Price(CurrencyType.POKECOINS, 5000));
		put("flamecharge", new Price(CurrencyType.POKECOINS, 5000));
		put("flamethrower", new Price(CurrencyType.POKECOINS, 5000));
		put("flareblitz", new Price(CurrencyType.POKECOINS, 5000));
		put("flash", new Price(CurrencyType.POKECOINS, 5000));
		put("flashcannon", new Price(CurrencyType.POKECOINS, 5000));
		put("fling", new Price(CurrencyType.POKECOINS, 5000));
		put("flipturn", new Price(CurrencyType.POKECOINS, 5000));
		put("fly", new Price(CurrencyType.POKECOINS, 5000));
		put("focusblast", new Price(CurrencyType.POKECOINS, 5000));
		put("focusenergy", new Price(CurrencyType.POKECOINS, 5000));
		put("focuspunch", new Price(CurrencyType.POKECOINS, 5000));
		put("foulplay", new Price(CurrencyType.POKECOINS, 5000));
		put("frenzyplant", new Price(CurrencyType.POKECOINS, 5000));
		put("frostbreath", new Price(CurrencyType.POKECOINS, 5000));
		put("furycutter", new Price(CurrencyType.POKECOINS, 5000));
		put("futuresight", new Price(CurrencyType.POKECOINS, 5000));
		put("gastroacid", new Price(CurrencyType.POKECOINS, 5000));
		put("gigadrain", new Price(CurrencyType.POKECOINS, 5000));
		put("gigaimpact", new Price(CurrencyType.POKECOINS, 5000));
		put("grassknot", new Price(CurrencyType.POKECOINS, 5000));
		put("grasspledge", new Price(CurrencyType.POKECOINS, 5000));
		put("grassyglide", new Price(CurrencyType.POKECOINS, 5000));
		put("grassyterrain", new Price(CurrencyType.POKECOINS, 5000));
		put("gravity", new Price(CurrencyType.POKECOINS, 5000));
		put("guardswap", new Price(CurrencyType.POKECOINS, 5000));
		put("gunkshot", new Price(CurrencyType.POKECOINS, 5000));
		put("gyroball", new Price(CurrencyType.POKECOINS, 5000));
		put("hail", new Price(CurrencyType.POKECOINS, 5000));
		put("hardpress", new Price(CurrencyType.POKECOINS, 5000));
		put("haze", new Price(CurrencyType.POKECOINS, 5000));
		put("headbutt", new Price(CurrencyType.POKECOINS, 5000));
		put("healbell", new Price(CurrencyType.POKECOINS, 5000));
		put("heatcrash", new Price(CurrencyType.POKECOINS, 5000));
		put("heatwave", new Price(CurrencyType.POKECOINS, 5000));
		put("heavyslam", new Price(CurrencyType.POKECOINS, 5000));
		put("helpinghand", new Price(CurrencyType.POKECOINS, 5000));
		put("hex", new Price(CurrencyType.POKECOINS, 5000));
		put("highhorsepower", new Price(CurrencyType.POKECOINS, 5000));
		put("honeclaws", new Price(CurrencyType.POKECOINS, 5000));
		put("hurricane", new Price(CurrencyType.POKECOINS, 5000));
		put("hydrocannon", new Price(CurrencyType.POKECOINS, 5000));
		put("hydropump", new Price(CurrencyType.POKECOINS, 5000));
		put("hyperbeam", new Price(CurrencyType.POKECOINS, 5000));
		put("hypervoice", new Price(CurrencyType.POKECOINS, 5000));
		put("iceball", new Price(CurrencyType.POKECOINS, 5000));
		put("icebeam", new Price(CurrencyType.POKECOINS, 5000));
		put("icefang", new Price(CurrencyType.POKECOINS, 5000));
		put("icepunch", new Price(CurrencyType.POKECOINS, 5000));
		put("icespinner", new Price(CurrencyType.POKECOINS, 5000));
		put("iciclespear", new Price(CurrencyType.POKECOINS, 5000));
		put("icywind", new Price(CurrencyType.POKECOINS, 5000));
		put("imprison", new Price(CurrencyType.POKECOINS, 5000));
		put("incinerate", new Price(CurrencyType.POKECOINS, 5000));
		put("infestation", new Price(CurrencyType.POKECOINS, 5000));
		put("irondefense", new Price(CurrencyType.POKECOINS, 5000));
		put("ironhead", new Price(CurrencyType.POKECOINS, 5000));
		put("irontail", new Price(CurrencyType.POKECOINS, 5000));
		put("knockoff", new Price(CurrencyType.POKECOINS, 5000));
		put("laserfocus", new Price(CurrencyType.POKECOINS, 5000));
		put("lashout", new Price(CurrencyType.POKECOINS, 5000));
		put("lastresort", new Price(CurrencyType.POKECOINS, 5000));
		put("leafblade", new Price(CurrencyType.POKECOINS, 5000));
		put("leafstorm", new Price(CurrencyType.POKECOINS, 5000));
		put("leechlife", new Price(CurrencyType.POKECOINS, 5000));
		put("lightscreen", new Price(CurrencyType.POKECOINS, 5000));
		put("liquidation", new Price(CurrencyType.POKECOINS, 5000));
		put("lowkick", new Price(CurrencyType.POKECOINS, 5000));
		put("lowsweep", new Price(CurrencyType.POKECOINS, 5000));
		put("lunge", new Price(CurrencyType.POKECOINS, 5000));
		put("magicalleaf", new Price(CurrencyType.POKECOINS, 5000));
		put("magiccoat", new Price(CurrencyType.POKECOINS, 5000));
		put("magicroom", new Price(CurrencyType.POKECOINS, 5000));
		put("magnetrise", new Price(CurrencyType.POKECOINS, 5000));
		put("megahorn", new Price(CurrencyType.POKECOINS, 5000));
		put("megakick", new Price(CurrencyType.POKECOINS, 5000));
		put("megapunch", new Price(CurrencyType.POKECOINS, 5000));
		put("metalclaw", new Price(CurrencyType.POKECOINS, 5000));
		put("metalsound", new Price(CurrencyType.POKECOINS, 5000));
		put("meteorbeam", new Price(CurrencyType.POKECOINS, 5000));
		put("metronome", new Price(CurrencyType.POKECOINS, 5000));
		put("mistyexplosion", new Price(CurrencyType.POKECOINS, 5000));
		put("mistyterrain", new Price(CurrencyType.POKECOINS, 5000));
		put("muddywater", new Price(CurrencyType.POKECOINS, 5000));
		put("mudshot", new Price(CurrencyType.POKECOINS, 5000));
		put("mudslap", new Price(CurrencyType.POKECOINS, 5000));
		put("mysticalfire", new Price(CurrencyType.POKECOINS, 5000));
		put("nastyplot", new Price(CurrencyType.POKECOINS, 5000));
		put("naturalgift", new Price(CurrencyType.POKECOINS, 5000));
		put("naturepower", new Price(CurrencyType.POKECOINS, 5000));
		put("nightshade", new Price(CurrencyType.POKECOINS, 5000));
		put("ominouswind", new Price(CurrencyType.POKECOINS, 5000));
		put("outrage", new Price(CurrencyType.POKECOINS, 5000));
		put("overheat", new Price(CurrencyType.POKECOINS, 5000));
		put("painsplit", new Price(CurrencyType.POKECOINS, 5000));
		put("payback", new Price(CurrencyType.POKECOINS, 5000));
		put("payday", new Price(CurrencyType.POKECOINS, 5000));
		put("petalblizzard", new Price(CurrencyType.POKECOINS, 5000));
		put("phantomforce", new Price(CurrencyType.POKECOINS, 5000));
		put("pinmissile", new Price(CurrencyType.POKECOINS, 5000));
		put("playrough", new Price(CurrencyType.POKECOINS, 5000));
		put("pluck", new Price(CurrencyType.POKECOINS, 5000));
		put("poisonjab", new Price(CurrencyType.POKECOINS, 5000));
		put("poisontail", new Price(CurrencyType.POKECOINS, 5000));
		put("pollenpuff", new Price(CurrencyType.POKECOINS, 5000));
		put("poltergeist", new Price(CurrencyType.POKECOINS, 5000));
		put("pounce", new Price(CurrencyType.POKECOINS, 5000));
		put("powergem", new Price(CurrencyType.POKECOINS, 5000));
		put("powershift", new Price(CurrencyType.POKECOINS, 5000));
		put("powerswap", new Price(CurrencyType.POKECOINS, 5000));
		put("poweruppunch", new Price(CurrencyType.POKECOINS, 5000));
		put("powerwhip", new Price(CurrencyType.POKECOINS, 5000));
		put("protect", new Price(CurrencyType.POKECOINS, 5000));
		put("psybeam", new Price(CurrencyType.POKECOINS, 5000));
		put("psychic", new Price(CurrencyType.POKECOINS, 5000));
		put("psychicfangs", new Price(CurrencyType.POKECOINS, 5000));
		put("psychicnoise", new Price(CurrencyType.POKECOINS, 5000));
		put("psychicterrain", new Price(CurrencyType.POKECOINS, 5000));
		put("psychocut", new Price(CurrencyType.POKECOINS, 5000));
		put("psychup", new Price(CurrencyType.POKECOINS, 5000));
		put("psyshock", new Price(CurrencyType.POKECOINS, 5000));
		put("quash", new Price(CurrencyType.POKECOINS, 5000));
		put("raindance", new Price(CurrencyType.POKECOINS, 5000));
		put("razorshell", new Price(CurrencyType.POKECOINS, 5000));
		put("recycle", new Price(CurrencyType.POKECOINS, 5000));
		put("reflect", new Price(CurrencyType.POKECOINS, 5000));
		put("rest", new Price(CurrencyType.POKECOINS, 5000));
		put("retaliate", new Price(CurrencyType.POKECOINS, 5000));
		put("revenge", new Price(CurrencyType.POKECOINS, 5000));
		put("reversal", new Price(CurrencyType.POKECOINS, 5000));
		put("risingvoltage", new Price(CurrencyType.POKECOINS, 5000));
		put("roar", new Price(CurrencyType.POKECOINS, 5000));
		put("rockblast", new Price(CurrencyType.POKECOINS, 5000));
		put("rockclimb", new Price(CurrencyType.POKECOINS, 5000));
		put("rockpolish", new Price(CurrencyType.POKECOINS, 5000));
		put("rockslide", new Price(CurrencyType.POKECOINS, 5000));
		put("rocksmash", new Price(CurrencyType.POKECOINS, 5000));
		put("rocktomb", new Price(CurrencyType.POKECOINS, 5000));
		put("roleplay", new Price(CurrencyType.POKECOINS, 5000));
		put("rollout", new Price(CurrencyType.POKECOINS, 5000));
		put("roost", new Price(CurrencyType.POKECOINS, 5000));
		put("round", new Price(CurrencyType.POKECOINS, 5000));
		put("safeguard", new Price(CurrencyType.POKECOINS, 5000));
		put("sandstorm", new Price(CurrencyType.POKECOINS, 5000));
		put("sandtomb", new Price(CurrencyType.POKECOINS, 5000));
		put("scald", new Price(CurrencyType.POKECOINS, 5000));
		put("scaleshot", new Price(CurrencyType.POKECOINS, 5000));
		put("scaryface", new Price(CurrencyType.POKECOINS, 5000));
		put("scorchingsands", new Price(CurrencyType.POKECOINS, 5000));
		put("screech", new Price(CurrencyType.POKECOINS, 5000));
		put("secretpower", new Price(CurrencyType.POKECOINS, 5000));
		put("seedbomb", new Price(CurrencyType.POKECOINS, 5000));
		put("selfdestruct", new Price(CurrencyType.POKECOINS, 5000));
		put("shadowball", new Price(CurrencyType.POKECOINS, 5000));
		put("shadowclaw", new Price(CurrencyType.POKECOINS, 5000));
		put("shockwave", new Price(CurrencyType.POKECOINS, 5000));
		put("signalbeam", new Price(CurrencyType.POKECOINS, 5000));
		put("silverwind", new Price(CurrencyType.POKECOINS, 5000));
		put("skillswap", new Price(CurrencyType.POKECOINS, 5000));
		put("skittersmack", new Price(CurrencyType.POKECOINS, 5000));
		put("skyattack", new Price(CurrencyType.POKECOINS, 5000));
		put("skydrop", new Price(CurrencyType.POKECOINS, 5000));
		put("sleeptalk", new Price(CurrencyType.POKECOINS, 5000));
		put("sludgebomb", new Price(CurrencyType.POKECOINS, 5000));
		put("sludgewave", new Price(CurrencyType.POKECOINS, 5000));
		put("smackdown", new Price(CurrencyType.POKECOINS, 5000));
		put("smartstrike", new Price(CurrencyType.POKECOINS, 5000));
		put("snarl", new Price(CurrencyType.POKECOINS, 5000));
		put("snatch", new Price(CurrencyType.POKECOINS, 5000));
		put("snore", new Price(CurrencyType.POKECOINS, 5000));
		put("snowscape", new Price(CurrencyType.POKECOINS, 5000));
		put("solarbeam", new Price(CurrencyType.POKECOINS, 5000));
		put("solarblade", new Price(CurrencyType.POKECOINS, 5000));
		put("speedswap", new Price(CurrencyType.POKECOINS, 5000));
		put("spikes", new Price(CurrencyType.POKECOINS, 5000));
		put("spite", new Price(CurrencyType.POKECOINS, 5000));
		put("stealthrock", new Price(CurrencyType.POKECOINS, 5000));
		put("steelbeam", new Price(CurrencyType.POKECOINS, 5000));
		put("steelroller", new Price(CurrencyType.POKECOINS, 5000));
		put("steelwing", new Price(CurrencyType.POKECOINS, 5000));
		put("stompingtantrum", new Price(CurrencyType.POKECOINS, 5000));
		put("stoneedge", new Price(CurrencyType.POKECOINS, 5000));
		put("storedpower", new Price(CurrencyType.POKECOINS, 5000));
		put("strength", new Price(CurrencyType.POKECOINS, 5000));
		put("stringshot", new Price(CurrencyType.POKECOINS, 5000));
		put("strugglebug", new Price(CurrencyType.POKECOINS, 5000));
		put("substitute", new Price(CurrencyType.POKECOINS, 5000));
		put("suckerpunch", new Price(CurrencyType.POKECOINS, 5000));
		put("sunnyday", new Price(CurrencyType.POKECOINS, 5000));
		put("supercellslam", new Price(CurrencyType.POKECOINS, 5000));
		put("superfang", new Price(CurrencyType.POKECOINS, 5000));
		put("superpower", new Price(CurrencyType.POKECOINS, 5000));
		put("surf", new Price(CurrencyType.POKECOINS, 5000));
		put("swagger", new Price(CurrencyType.POKECOINS, 5000));
		put("swift", new Price(CurrencyType.POKECOINS, 5000));
		put("swordsdance", new Price(CurrencyType.POKECOINS, 5000));
		put("synthesis", new Price(CurrencyType.POKECOINS, 5000));
		put("tailslap", new Price(CurrencyType.POKECOINS, 5000));
		put("tailwind", new Price(CurrencyType.POKECOINS, 5000));
		put("takedown", new Price(CurrencyType.POKECOINS, 5000));
		put("taunt", new Price(CurrencyType.POKECOINS, 5000));
		put("telekinesis", new Price(CurrencyType.POKECOINS, 5000));
		put("temperflare", new Price(CurrencyType.POKECOINS, 5000));
		put("terablast", new Price(CurrencyType.POKECOINS, 5000));
		put("terrainpulse", new Price(CurrencyType.POKECOINS, 5000));
		put("thief", new Price(CurrencyType.POKECOINS, 5000));
		put("throatchop", new Price(CurrencyType.POKECOINS, 5000));
		put("thunder", new Price(CurrencyType.POKECOINS, 5000));
		put("thunderbolt", new Price(CurrencyType.POKECOINS, 5000));
		put("thunderfang", new Price(CurrencyType.POKECOINS, 5000));
		put("thunderpunch", new Price(CurrencyType.POKECOINS, 5000));
		put("thunderwave", new Price(CurrencyType.POKECOINS, 5000));
		put("torment", new Price(CurrencyType.POKECOINS, 5000));
		put("toxic", new Price(CurrencyType.POKECOINS, 5000));
		put("toxicspikes", new Price(CurrencyType.POKECOINS, 5000));
		put("trailblaze", new Price(CurrencyType.POKECOINS, 5000));
		put("triattack", new Price(CurrencyType.POKECOINS, 5000));
		put("trick", new Price(CurrencyType.POKECOINS, 5000));
		put("trickroom", new Price(CurrencyType.POKECOINS, 5000));
		put("tripleaxel", new Price(CurrencyType.POKECOINS, 5000));
		put("twister", new Price(CurrencyType.POKECOINS, 5000));
		put("upperhand", new Price(CurrencyType.POKECOINS, 5000));
		put("uproar", new Price(CurrencyType.POKECOINS, 5000));
		put("uturn", new Price(CurrencyType.POKECOINS, 5000));
		put("vacuumwave", new Price(CurrencyType.POKECOINS, 5000));
		put("venomdrench", new Price(CurrencyType.POKECOINS, 5000));
		put("venoshock", new Price(CurrencyType.POKECOINS, 5000));
		put("voltswitch", new Price(CurrencyType.POKECOINS, 5000));
		put("waterfall", new Price(CurrencyType.POKECOINS, 5000));
		put("waterpledge", new Price(CurrencyType.POKECOINS, 5000));
		put("waterpulse", new Price(CurrencyType.POKECOINS, 5000));
		put("weatherball", new Price(CurrencyType.POKECOINS, 5000));
		put("whirlpool", new Price(CurrencyType.POKECOINS, 5000));
		put("wildcharge", new Price(CurrencyType.POKECOINS, 5000));
		put("willowisp", new Price(CurrencyType.POKECOINS, 5000));
		put("wonderroom", new Price(CurrencyType.POKECOINS, 5000));
		put("workup", new Price(CurrencyType.POKECOINS, 5000));
		put("worryseed", new Price(CurrencyType.POKECOINS, 5000));
		put("xscissor", new Price(CurrencyType.POKECOINS, 5000));
		put("zenheadbutt", new Price(CurrencyType.POKECOINS, 5000));
	}};

}
