package gg.mmorealms.module.economy.backend.common.dto;

import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.economy.backend.common.EconomyBackendModule;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import gg.mmorealms.module.economy.common.dto.Price;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IBalances extends ISavable {
	static @NotNull IBalances getByUUID(UUID uuid) {
		IBalances balances = EconomyBackendModule.instance().getBalanceLoader().getByIdentifier(uuid);

		if (balances == null) {
			balances = new Balances(uuid);
		}

		return balances;
	}

	static @NotNull IBalances getByUser(@NotNull IUser user) {
		return getByUUID(user.getUUID());
	}

	Double get(CurrencyType currency);

	void set(CurrencyType currency, Double amount, String source);

	default Boolean has(CurrencyType currency, Double amount) {
		return get(currency) >= amount;
	}

	default Boolean has(Price price) {
		return get(price.currency()) >= price.amount();
	}

	default void add(CurrencyType currency, Double amount, String source) {
		Double currentAmount = get(currency);
		set(currency, currentAmount + amount, source);
	}

	default void add(Price price, String source) {
		Double currentAmount = get(price.currency());
		set(price.currency(), currentAmount + price.amount(), source);
	}

	default void remove(CurrencyType currency, Double amount, String source) {
		Double currentAmount = get(currency);
		set(currency, currentAmount - amount, source);
	}

	default void remove(Price price, String source) {
		Double currentAmount = get(price.currency());
		set(price.currency(), currentAmount - price.amount(), source);
	}

	String toPrettyString(Boolean showHeader, Boolean showUnknownCurrencies);

	UUID getUUID();
}
