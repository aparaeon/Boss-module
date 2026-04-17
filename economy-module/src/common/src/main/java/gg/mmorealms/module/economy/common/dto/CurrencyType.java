package gg.mmorealms.module.economy.common.dto;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@Getter
public enum CurrencyType {
	// TODO properties like name, color, hidden, etc should be loaded from a config
	GEMS("Gems", "<light_purple><bold>", true),
	TOKENS("Tokens", "<gold><bold>", false),
	POKECOINS("PokeCoins", "<gradient:#FF0000:white><bold>", false);

	@Getter
	private static final List<String> friendlyNames = Arrays.stream(CurrencyType.values())
			.map(CurrencyType::getName)
			.toList();

	private final String name;
	private final String color;
	private final boolean hidden;

	CurrencyType(String name, String color, boolean hidden) {
		this.name = name;
		this.color = color;
		this.hidden = hidden;
	}

	public static @Nullable CurrencyType parse(String currency) {
		for (CurrencyType value : CurrencyType.values()) {
			if (value.getName().equalsIgnoreCase(currency) || value.name().equalsIgnoreCase(currency)) {
				return value;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String toPrettyString() {
		return this.color + this.name;
	}
}
