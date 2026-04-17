package gg.mmorealms.module.shop.backend.common.util;

import gg.mmorealms.module.shop.backend.common.ShopBackendModule;
import gg.mmorealms.module.shop.backend.common.shop.Shop;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

public class CommonMethods {
	/**
	 * Gets shop data by name.
	 *
	 * @param name The name of the shop to look for
	 * @return An object of class Shop from the list with the same case-sensitive name as the one
	 * passed by argument, or null if none is found.
	 */
	public static Shop getShopByName(String name) {
		for (Shop shop : ShopBackendModule.instance().config().shops) {
			if (shop.getName().equalsIgnoreCase(name)) {
				return shop;
			}
		}
		return null;
	}

	public static List<Shop> getAllShops() {
		return ShopBackendModule.instance().config().shops;
	}

	public static Item getItem(String id) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
	}

	public static Optional<Item> getOptionalItem(String id) {
		return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id));
	}

	public static Item getItem(String id, Item defaultItem) {
		return getOptionalItem(id).orElse(defaultItem);
	}

	public static String getItemId(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

}
