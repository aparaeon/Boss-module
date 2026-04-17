package gg.mmorealms.module.economy.common.dto;

import org.jetbrains.annotations.NotNull;

public record Price(CurrencyType currency, int amount) {

	public Price(int amount, CurrencyType type) {
		this(type, amount);
	}

	@Override
	public @NotNull String toString() {
		return amount + " " + currency.toPrettyString();
	}

	public Price discount(int amount) {
		return new Price(this.amount - amount, this.currency);
	}
}