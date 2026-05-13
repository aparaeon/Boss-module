package gg.mmorealms.module.core.backend.common.gui;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.module.core.backend.common.dto.GUIButton;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

@Setter
@Accessors(fluent = true, chain = true)
@Getter
public class GUISettings {

	private boolean manipulatePlayerSlots = false;
	private String menuTypeKey;

	private AsyncSettings async = new AsyncSettings();
	private AutoRefreshSettings autoRefresh = new AutoRefreshSettings();
	private PagedSettings paged = new PagedSettings();
	private DevSettings dev = new DevSettings();

	// -------------------- Feature settings --------------------

	@Setter @Getter @Accessors(fluent = true, chain = true)
	public static class AsyncSettings {
		private boolean enabled = true;
	}

	@Setter @Getter @Accessors(fluent = true, chain = true)
	public static class AutoRefreshSettings {
		private boolean enabled = false;
		private int tickInterval = 20;
	}

	@Setter @Getter @Accessors(fluent = true, chain = true)
	public static class PagedSettings {
		private boolean enabled = false;
		private boolean wrap = false;
		private GUIButton backPageButton = null;
		private GUIButton nextPageButton = null;

		public PagedSettings backPage(int position) {
			this.backPageButton
				.position(position);
			return this;
		}

		public PagedSettings nextPage(int position) {
			this.nextPageButton
				.position(position);
			return this;
		}
	}

	@Setter @Getter @Accessors(fluent = true, chain = true)
	public static class DevSettings {
		private boolean enabled = false;
		private boolean placeAllSlots = true;
	}

	public GUISettings() {
		ResourceLocation menuTypeResourceLocation = BuiltInRegistries.MENU.getKey(MenuType.GENERIC_9x1);

		if (menuTypeResourceLocation == null) {
			throw new RuntimeException("Failed to get the default menu type from BuiltInRegistries.MENU (MenuType.GENERIC_9x1).");
		}

		this.menuTypeKey = menuTypeResourceLocation.toString();
	}

	public GUISettings chestSize(int chestSize) {
		MenuType<ChestMenu> menuType = getChestMenuType(chestSize);

		if (menuType == null) {
			Logger.error("Failed to get the menu type for chest size: " + chestSize + ". Using default MenuType.GENERIC_9x1 instead.");
			return this;
		}

		ResourceLocation menuTypeResourceLocation = BuiltInRegistries.MENU.getKey(menuType);

		if (menuTypeResourceLocation == null) {
			Logger.error("Failed to get the menu type for chest size: " + chestSize + ". Using default MenuType.GENERIC_9x1 instead.");
			return this;
		}

		this.menuTypeKey = menuTypeResourceLocation.toString();
		return this;
	}

	private MenuType<ChestMenu> getChestMenuType(int size) {
		return switch (size) {
			case 1 -> MenuType.GENERIC_9x1;
			case 2 -> MenuType.GENERIC_9x2;
			case 3 -> MenuType.GENERIC_9x3;
			case 4 -> MenuType.GENERIC_9x4;
			case 5 -> MenuType.GENERIC_9x5;
			case 6 -> MenuType.GENERIC_9x6;
			default -> null;
		};
	}

	public MenuType<?> getMenuType() {
		MenuType<?> menuType = BuiltInRegistries.MENU.get(ResourceLocation.parse(this.menuTypeKey));

		if (menuType == null) {
			Logger.error("Failed to get the menu type for key: " + this.menuTypeKey + ". Using default MenuType.GENERIC_9x1 instead.");
			return MenuType.GENERIC_9x1;
		}

		return menuType;
	}
}