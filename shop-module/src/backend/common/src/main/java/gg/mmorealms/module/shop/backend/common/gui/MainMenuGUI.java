package gg.mmorealms.module.shop.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.gui.GUI;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.config.ShopConfig;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;

public class MainMenuGUI extends GUI {

	private static final ShopConfig CONFIG = ShopBackendModule.instance().config();

	public MainMenuGUI(User user) {
		super(user, CONFIG.shopGUI.settings);
	}

	@Override
	public String getTitleString() {
		return CONFIG.shopGUI.mainMenuTitle;
	}

	@Override
	public void setup() {
		setButton(CONFIG.shopGUI.background);

		setButton(
				CONFIG.shopGUI.back
						.position(CONFIG.shopGUI.getShopSize() + CONFIG.shopGUI.mainMenuBackOffset)
						.onClick(this::close)
		);

		for (Shop shop : CommonMethods.getAllShops()) {
			if (shop.getSlot() == -1) {
				continue;
			}

			setButton(
					new GUIButton()
							.displayName(shop.getName())
							.display(shop.getDisplayItem())
							.position(shop.getSlot())
							.onClick(click -> openShop(shop))
			);
		}

	}

	private void openShop(Shop shop) {
		if (shop.getItems().size() > PagedShopGUI.MAX_PAGE_ITEMS) {
			new PagedShopGUI(user, shop, true);
			return;
		}

		new ShopGUI(user, shop, true);
	}
}
