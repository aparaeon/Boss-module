package gg.mmorealms.module.shop.backend.common.gui;

import gg.mmorealms.module.core.backend.common.dto.ClickType;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import gg.mmorealms.module.shop.backend.common.util.CommonMethods;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class PagedShopGUI extends ShopGUI {

	public static final int MAX_PAGE_ITEMS = ITEM_SLOTS;
	private static final int PREVIOUS_PAGE_SLOT = CONFIG.shopGUI.getShopSize() + CONFIG.shopGUI.shopPreviousPageOffset;
	private static final int CURRENT_PAGE_SLOT = CONFIG.shopGUI.getShopSize() + CONFIG.shopGUI.shopCurrentPageOffset;
	private static final int NEXT_PAGE_SLOT = CONFIG.shopGUI.getShopSize() + CONFIG.shopGUI.shopNextPageOffset;
	protected int page = 1;
	protected int maxPage;

	public PagedShopGUI(User user, Shop shop) {
		super(user, shop);
	}

	public PagedShopGUI(User user, Shop shop, boolean goBackToMainMenu) {
		super(user, shop, goBackToMainMenu);
	}

	@Override
	public void setup() {
		if (page == 0) {
			page = 1;
		}

		maxPage = (int) Math.ceil((double) shop.getItems().size() / MAX_PAGE_ITEMS);

		super.setup();

		HashMap<String, Object> placeholders = new HashMap<>() {{
			put("currentPage", page);
		}};

		setButton(
				CONFIG.shopGUI.currentPage
						.position(CURRENT_PAGE_SLOT)
						.display(new ItemStack(CommonMethods.getItem(CONFIG.shopGUI.currentPageItem), page))
						.placeholders(placeholders)
		);

		setButton(
				CONFIG.shopGUI.nextPage
						.position(NEXT_PAGE_SLOT)
						.onClick(this::nextPage)
		);
	}

	private void nextPage(ClickType click) {
		int oldPage = this.page;
		this.page = getNextPage();

		if (oldPage != page) {
			refresh();
		}
	}

	private void previousPage(ClickType click) {
		int oldPage = this.page;
		this.page = getPreviousPage();

		if (oldPage != page) {
			refresh();
		}
	}

	@Override
	public void renderAllItems() {
		int n = MAX_PAGE_ITEMS * (page);

		for (int i = MAX_PAGE_ITEMS * (page - 1); i < n; i++) {
			int slotIndex = i - (MAX_PAGE_ITEMS * (page - 1));
			if (i < shop.getItems().size()) {
				renderItemSlot(slotIndex, i);
			}
		}

		HashMap<String, Object> placeholders = new HashMap<>() {{
			put("currentPage", page);
		}};

		setButton(
				CONFIG.shopGUI.currentPage.clone()
						.position(CURRENT_PAGE_SLOT)
						.display(new ItemStack(CommonMethods.getItem(CONFIG.shopGUI.currentPageItem), page))
						.placeholders(placeholders)
		);

		if (page != 1) {

			setButton(
					CONFIG.shopGUI.previousPage.clone()
							.position(PREVIOUS_PAGE_SLOT)
							.onClick(this::previousPage)
			);
		}

		if (page != maxPage) {
			setButton(
					CONFIG.shopGUI.nextPage.clone()
							.position(NEXT_PAGE_SLOT)
							.onClick(this::nextPage)
			);
		}
	}


	public int getPreviousPage() {
		return Math.max(1, page - 1);
	}

	public int getNextPage() {
		return Math.min(page + 1, maxPage);
	}
}
