package gg.mmorealms.module.shop.backend.common.shop;

import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.shop.backend.common.ShopBackendModule;

import java.util.ArrayList;
import java.util.List;

public class ShopManager {
	private List<String> allShopsNames = null;

	public List<String> getAllShopsNames() {
		if (allShopsNames == null) {
			reloadShopsNames();
		}

		return allShopsNames;
	}

	public void addShop(String name, CurrencyType currency) {
		ShopBackendModule.instance().config().shops.add(new Shop(name, currency));

		allShopsNames.add(name);
	}

	public boolean removeShop(String name) {
		for (Shop shop : ShopBackendModule.instance().config().shops) {
			if (shop.getName().equalsIgnoreCase(name)) {
				ShopBackendModule.instance().config().shops.remove(shop);
				allShopsNames.remove(name);
				return true;
			}
		}
		return false;

	}

	public void reloadShopsNames() {
		allShopsNames = new ArrayList<>();
		for (Shop shop : ShopBackendModule.instance().config().shops) {
			allShopsNames.add(shop.getName());
		}
	}

}
