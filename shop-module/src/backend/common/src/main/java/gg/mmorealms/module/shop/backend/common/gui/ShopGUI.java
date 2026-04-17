package gg.mmorealms.module.shop.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import gg.mmorealms.module.shop.backend.common.economy.Transaction;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.shop.ShopItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class ShopGUI extends GUI {

	protected static final ShopConfig CONFIG = ShopBackendModule.instance().config();
	protected static final int COLUMNS = CONFIG.shopGUI.shopColumns;
	protected static final int ITEM_ROWS = CONFIG.shopGUI.shopItemRows; // At least one row must be reserved for menu buttons
	protected static final int ITEM_SLOTS = ITEM_ROWS * COLUMNS;

	private static final int BALANCE_SLOT = CONFIG.shopGUI.getShopSize() + CONFIG.shopGUI.shopBalanceOffset;
	private static final int BACK_SLOT = CONFIG.shopGUI.getShopSize() + CONFIG.shopGUI.shopBackOffset;

	private final boolean goBackToMainMenu;
	protected Shop shop;

	/**
	 * Constructs a new simple container gui for the supplied player.
	 *
	 * @param user the player to server this gui to
	 * @param shop the shop
	 *             will be treated as slots of this gui
	 */
	public ShopGUI(User user, Shop shop) {
		this(user, shop, false);
	}

	public ShopGUI(User user, Shop shop, boolean goBackToMainMenu) {
		super(user, CONFIG.shopGUI.settings);
		this.shop = shop;
		this.goBackToMainMenu = goBackToMainMenu;

		open();
	}

	@Override
	public String getTitleString() {
		return CONFIG.shopGUI.shopTitle.parse("shopName", shop.getName()).parse();
	}

	@Override
	public void setup() {
		setButton(CONFIG.shopGUI.background);

		setButton(
				CONFIG.shopGUI.back
						.position(BACK_SLOT)
						.onClick(this::goBackAction)
		);

		renderPlayerBalanceSlot();
		renderAllItems();
	}

	private void renderPlayerBalanceSlot() {
		IBalances balances = IBalances.getByUser(user);

		HashMap<String, Object> placeholders = new HashMap<>() {{
			put("user", user.getUsername());
			put("amount", NumberUtils.formatNumberWithUnitsPrecise(balances.get(shop.getDefaultCurrency())));
			put("currency_color", shop.getDefaultCurrency().getColor());
			put("currency", shop.getDefaultCurrency());
		}};

		setButton(
				CONFIG.shopGUI.balance
						.position(BALANCE_SLOT)
						.onClick(this::nop)
						.placeholders(placeholders)
		);
	}

	protected void renderAllItems() {
		for (int i = 0; i < Math.min(shop.getItems().size(), ITEM_SLOTS); i++) {
			renderItemSlot(i);
		}
	}

	protected void renderItemSlot(int slotIndex) {
		renderItemSlot(slotIndex, slotIndex);
	}

	protected void renderItemSlot(int slotIndex, int shopItemIndex) {
		ShopItem item = shop.getItems().get(shopItemIndex);

		ItemStack guiItem = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item.itemId())));

		setButton(
				new GUIButton()
						.display(guiItem)
						.displayName(item.getName())
						.lore(item.getLore())
						.position(slotIndex)
						.onClick(click -> action(click, item))
		);
	}

	private void action(ClickType click, ShopItem item) {
		Transaction transaction = new Transaction(user, shop);

		switch (click) {
			case MOUSE_LEFT -> transaction.buyItem(item, false);
			case MOUSE_RIGHT -> transaction.sellItem(item, false);
			case MOUSE_LEFT_SHIFT -> transaction.buyItem(item, true);
			case MOUSE_RIGHT_SHIFT -> transaction.sellItem(item, true);
		}

		refresh();
	}

	public void goBackAction(ClickType click) {
		this.close();

		if (goBackToMainMenu) {
			new MainMenuGUI(user).open();
		}
	}
}


