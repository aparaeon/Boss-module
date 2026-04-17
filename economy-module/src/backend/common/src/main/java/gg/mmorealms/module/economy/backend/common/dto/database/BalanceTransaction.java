package gg.mmorealms.module.economy.backend.common.dto.database;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.economy.backend.common.EconomyBackendModule;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.common.dto.CurrencyType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity(name = "balance_transaction")
@NoArgsConstructor
public class BalanceTransaction implements IDatabaseEntry<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int transactionID;
	private UUID user;
	private Long timestamp;
	private double amount;
	private double runningBalance; // After the transaction has completed
	@JdbcTypeCode(SqlTypes.JSON)
	private CurrencyType currency;
	private String source;

	public BalanceTransaction(IBalances wallet, double delta, CurrencyType currency, String source) {
		this.user = wallet.getUUID();
		this.timestamp = System.currentTimeMillis();
		this.amount = delta;
		this.currency = currency;
		this.source = source;
		this.runningBalance = wallet.get(currency) + delta;

		Logger.info(
				new MessageBuilder("{user} {verb} {amount} {currency} from {source}. Running balance: {running_balance}")
						.parse("user", this.user.toString())
						.parse("verb", delta > 0 ? "received" : "spent")
						.parse("amount", String.format("%.2f", this.amount))
						.parse("currency", this.currency.name())
						.parse("source", this.source)
						.parse("running_balance", this.runningBalance)
		);

		if (EconomyBackendModule.instance().getConfig().trackedCurrencies.contains(currency)) {
			try {
				save();
			} catch (DatabaseSaveException exception) {
				Logger.error(exception);
			}
		}
	}

	@Override
	public Integer getIdentifier() {
		return transactionID;
	}

	@Override
	public DatabaseLoader<Integer, ?, ?> getLoader() {
		return null;
	}
}
