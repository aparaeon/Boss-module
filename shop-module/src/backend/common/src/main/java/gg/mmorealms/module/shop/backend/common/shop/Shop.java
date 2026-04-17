package gg.mmorealms.module.shop.backend.common.shop;

import gg.mmorealms.module.economy.common.dto.CurrencyType;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A shop which holds a list of items
 */
@Getter
@Builder
public class Shop {
	protected final String name;
	protected final List<ShopItem> items;
	protected final CurrencyType defaultCurrency;
	protected String displayItem;
	protected int slot;

	public Shop(String name, List<ShopItem> items, CurrencyType defaultCurrency, String displayItem, int slot) {
		this.name = name;
		this.items = items;
		this.defaultCurrency = defaultCurrency;
		this.displayItem = displayItem;
		this.slot = slot;
	}

	public Shop(String name, CurrencyType defaultCurrency) {
		this(name, new ArrayList<>(), defaultCurrency);
	}

	public Shop(String name, List<ShopItem> items, @Nullable CurrencyType defaultCurrency) {
		this.name = name;
		this.items = items;
		this.defaultCurrency = defaultCurrency;
	}

	public ShopItem findItem(ItemStack items) {
		String itemId = BuiltInRegistries.ITEM.getKey(items.getItem()).toString();
		for (ShopItem shopItem : this.items) {
			if (shopItem.itemId().equals(itemId) && shopItem.matches(items)) {
				return shopItem;
			}
		}
		return null;
	}

	public Item getDisplayItem() {
		if (displayItem == null || displayItem.isEmpty()) {
			return BuiltInRegistries.ITEM.get(ResourceLocation.parse(items.getFirst().itemId())).asItem();
		}

		return BuiltInRegistries.ITEM.get(ResourceLocation.parse(displayItem)).asItem();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Shop shop = (Shop) o;

		return name.equals(shop.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
