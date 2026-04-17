package gg.mmorealms.module.crates.backend.common.config;

import com.raduvoinea.utils.generic.dto.Pair2;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderList;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.crates.backend.common.dto.Crate;
import gg.mmorealms.module.crates.backend.common.dto.CrateItem;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.common.dto.Price;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CratesConfig {

	public MessageBuilderList displayItemLore = new MessageBuilderList(List.of(
			"<yellow>Chance: {chance}%"
	));
	public MessageBuilder crateLootLore = new MessageBuilder("<gray>- <white>{name} <yellow>{chance}%");
	public MessageBuilder hasKeysPrice = new MessageBuilder("<white>Keys: <aqua>{keys}");
	public MessageBuilder noKeysPrice = new MessageBuilder("<white>Price: <aqua>{price} Gems");
	public MessageBuilder notPurchasable = new MessageBuilder("<red>Not Purchasable");
	public String fullInventory = "<red>Please make sure that you have at least 1 free slot in inventory";

	public MessageBuilder getCratePriceMessage(int keys, Price price) {
		if (keys == 0) {
			if (price.amount() <= 0) {
				return notPurchasable;
			}
			return noKeysPrice;
		}
		return hasKeysPrice;
	}

	public List<Crate> crates = List.of(
			new Crate(
					"red_crate",
					"<red>Red <white>Crate",
					List.of(
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIRT, 20))
											.displayName("20x Dirt"),
									new MessageBuilderList(List.of(
											"give {user} dirt 20",
											"say Hello"
									)),
									30
							),
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIAMOND, 10))
											.displayName("10x Diamond"),
									new MessageBuilderList(List.of("give {user} diamond 10")),
									5
							),
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.IRON_INGOT, 30))
											.displayName("30x Iron Ingot"),
									new MessageBuilderList(List.of("give {user} iron_ingot 30")),
									25
							),
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.COPPER_INGOT, 15))
											.displayName("15x Copper Ingot"),
									new MessageBuilderList(List.of("give {user} copper_ingot 15")),
									100
							),
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.ELYTRA, 1))
											.displayName("1x Elytra"),
									new MessageBuilderList(List.of("give {user} elytra 1")),
									25
							)
					),
					new GUIButton()
							.display(new ItemStack(Items.RED_STAINED_GLASS_PANE))
							.displayName("<red>Red <white>Crate")
							.lore(List.of(
									"{loot}",
									"",
									"<white>Keys: <aqua>{keys}",
									"<white>Price: <aqua>{price} Gems"
							))
							.position(0, 0, 3, 3),
					new Price(CurrencyType.GEMS, 1000),
					Location.of(12, 61, -25)
			),

			new Crate(
					"lime_crate",
					"<green>Lime <white>Crate",
					List.of(
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIRT, 1))
											.displayName("1x Dirt"),
									new MessageBuilderList(List.of("give {user} dirt 1")),
									30
							)
					),
					new GUIButton()
							.display(new ItemStack(Items.LIME_STAINED_GLASS_PANE))
							.displayName("<green>Lime <white>Crate")
							.lore(List.of(
									"{loot}",
									"",
									"<white>Keys: <aqua>{keys}",
									"<white>Price: <aqua>{price} Gems"
							))
							.position(0, 3, 3, 3),
					new Price(CurrencyType.GEMS, 1500),
					Location.of(11, 61, -29)
			),

			new Crate(
					"yellow_crate",
					"<yellow>Yellow <white>Crate",
					List.of(
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIRT, 1))
											.displayName("1x Dirt"),
									new MessageBuilderList(List.of("give {user} dirt 1")),
									30
							)
					),
					new GUIButton()
							.display(new ItemStack(Items.YELLOW_STAINED_GLASS_PANE))
							.displayName("<yellow>Yellow <white>Crate")
							.lore(List.of(
									"{loot}",
									"",
									"<white>Keys: <aqua>{keys}",
									"<white>Price: <aqua>{price} Gems"
							))
							.position(0, 6, 3, 3),
					new Price(CurrencyType.GEMS, 2000),
					Location.of(14, 61, -33)
			),

			new Crate(
					"pink_crate",
					"<light_purple>Pink <white>Crate",
					List.of(
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIRT, 1))
											.displayName("1x Dirt"),
									new MessageBuilderList(List.of("give {user} dirt 1")),
									30
							)
					),
					new GUIButton()
							.display(new ItemStack(Items.PINK_STAINED_GLASS_PANE))
							.displayName("<light_purple>Pink <white>Crate")
							.lore(List.of(
									"{loot}",
									"",
									"<white>Keys: <aqua>{keys}",
									"<white>Price: <aqua>{price} Gems"
							))
							.position(3, 0, 3, 3),
					new Price(CurrencyType.GEMS, 2500),
					Location.of(18, 61, -36)
			),

			new Crate(
					"purple_crate",
					"<dark_purple>Purple <white>Crate",
					List.of(
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIRT, 1))
											.displayName("1x Dirt"),
									new MessageBuilderList(List.of("give {user} dirt 1")),
									30
							)
					),
					new GUIButton()
							.display(new ItemStack(Items.PURPLE_STAINED_GLASS_PANE))
							.displayName("<dark_purple>Purple <white>Crate")
							.lore(List.of(
									"{loot}",
									"",
									"<white>Keys: <aqua>{keys}",
									"<white>Price: <aqua>{price} Gems"
							))
							.position(3, 3, 3, 3),
					new Price(CurrencyType.GEMS, 3000),
					Location.of(22, 61, -35)
			),

			new Crate(
					"gray_crate",
					"<gray>Gray <white>Crate",
					List.of(
							new CrateItem(
									new GUIButton()
											.display(new ItemStack(Items.DIRT, 1))
											.displayName("1x Dirt"),
									new MessageBuilderList(List.of("give {user} dirt 1")),
									30
							)
					),
					new GUIButton()
							.display(new ItemStack(Items.GRAY_STAINED_GLASS_PANE))
							.displayName("<gray>Gray <white>Crate")
							.lore(List.of(
									"{loot}",
									"",
									"{price}"
							))
							.position(3, 6, 3, 3),
					new Price(CurrencyType.GEMS, 5000),
					Location.of(24, 61, -23)
			)
	);

	public CratesGUI cratesGUI = new CratesGUI();
	public CratePreviewGUI cratePreviewGUI = new CratePreviewGUI();
	public CrateGUI crateGUI = new CrateGUI();

	public static class CratesGUI {
		public String title = "Crates";
	}

	public static class CratePreviewGUI {
		public MessageBuilder title = new MessageBuilder("{name}");
		public GUI.Settings settings = new GUI.Settings().chestSize(5);

		public List<Integer> lootPositions = List.of(
				10, 11, 12, 13, 14, 15, 16,
				19, 20, 21, 22, 23, 24, 25,
				28, 29, 30, 31, 32, 33, 34,
				37, 38, 39, 40, 41, 42, 43
		);

		public GUIButton background = new GUIButton()
				.display(Items.WHITE_STAINED_GLASS_PANE)
				.position(
						0, 1, 2, 3, 4, 5, 6, 7, 8,
						9, 17,
						18, 26,
						27, 35,
						36, 37, 38, 39, 40, 41, 42, 43, 44
				);

		public GUIButton closeItem = new GUIButton()
				.display(Items.RED_STAINED_GLASS_PANE)
				.displayName("<red>Close")
				.position(39);

		public GUIButton openItem = new GUIButton()
				.display(Items.GREEN_STAINED_GLASS_PANE)
				.displayName("<green>Open")
				.lore(List.of(
						"",
						"{price}"
				))
				.position(41);

	}

	public static class CrateGUI {
		public MessageBuilder title = new MessageBuilder("{name}");
		public GUI.Settings settings = new GUI.Settings().chestSize(5);

		public List<Integer> lootPositions = List.of(
				19, 20, 21, 22, 23, 24, 25
		);

		public GUIButton background = new GUIButton()
				.display(Items.WHITE_STAINED_GLASS_PANE)
				.position(
						0, 1, 2, 3, 4, 5, 6, 7, 8,
						9, 17,
						18, 26,
						27, 35,
						36, 37, 38, 39, 40, 41, 42, 43, 44
				);

		public GUIButton indicator = new GUIButton()
				.displayName("")
				.display(Items.LIME_STAINED_GLASS_PANE)
				.position(1, 4, 1, 3);


		public List<Pair2<Range, Integer>> animationDelays = List.of(
				new Pair2<>(Range.of(0, 30), 1),
				new Pair2<>(Range.of(31, 60), 2),
				new Pair2<>(Range.of(61, 90), 3),
				new Pair2<>(Range.of(91, 110), 4),
				new Pair2<>(Range.of(111, 120), 5),
				new Pair2<>(Range.of(121, 130), 6),
				new Pair2<>(Range.of(131, 140), 10),
				new Pair2<>(Range.of(141, 145), 15),
				new Pair2<>(Range.of(146, 150), 20),
				new Pair2<>(Range.of(151, 155), 40)
		);

		public int maxTicks = 155;
	}

	public @Nullable Crate getCrate(String id) {
		for (Crate crate : crates) {
			if (crate.id.equals(id)) {
				return crate;
			}
		}

		return null;
	}


}
