package gg.mmorealms.module.economy.backend.common.dto;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.core.common.utils.NumberUtils;
import gg.mmorealms.module.economy.backend.common.EconomyBackendModule;
import gg.mmorealms.module.economy.backend.common.config.EconomyConfig;
import gg.mmorealms.module.economy.backend.common.dto.database.BalanceTransaction;
import gg.mmorealms.module.economy.backend.common.manager.BalanceLoader;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

@Entity(name = "user_balances")
@NoArgsConstructor
@Getter
public class Balances implements IDatabaseEntry<UUID>, IBalances {
	@Id
	@NotNull
	protected UUID uuid;
	@JdbcTypeCode(SqlTypes.JSON)
	protected HashMap<CurrencyType, Double> balances = new HashMap<>();

	public Balances(@NotNull UUID uuid) {
		this.uuid = uuid;

		getLoader().cache(uuid, this);
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public BalanceLoader getLoader() {
		return EconomyBackendModule.instance().getBalanceLoader();
	}

	public String toPrettyString(Boolean showHeader, Boolean showUnknownCurrencies) {
		EconomyConfig config = EconomyBackendModule.instance().getConfig();

		if (balances.isEmpty()) {
			return config.lang.emptyBalance;
		}

		StringBuilder stringBuilder = new StringBuilder();

		if (showHeader) {
			stringBuilder.append(config.lang.headerBalance);
		}

		for (CurrencyType currency : balances.keySet()) {
			Double amount = get(currency);

			stringBuilder.append(config.lang.entryBalance
					.parse("currencyColor", currency.getColor())
					.parse("currency", currency.getName())
					.parse("amount", NumberUtils.formatNumberWithUnits(amount))
			);
		}

		return stringBuilder.toString();
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}


	@Override
	public Double get(CurrencyType currency) {
		return balances.getOrDefault(currency, 0.0);
	}

	@Override
	public void set(CurrencyType currency, Double amount, String source) {
		if (amount <= 0) {
			balances.remove(currency);
			return;
		}

		double delta = -(balances.getOrDefault(currency, 0.0) - amount);
		new BalanceTransaction(this, delta, currency, source);

		balances.put(currency, amount);
	}
}
