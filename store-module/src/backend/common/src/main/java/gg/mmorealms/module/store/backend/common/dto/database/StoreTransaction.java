package gg.mmorealms.module.store.backend.common.dto.database;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.economy.common.dto.Price;
import gg.mmorealms.module.store.backend.common.dto.StoreEntry;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity(name = "store_transaction")
@NoArgsConstructor
public class StoreTransaction implements IDatabaseEntry<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int transactionID;
	private String packageID;
	private UUID user;
	private UUID target;
	private Long timestamp;

	public StoreTransaction(StoreEntry entry, UUID user, UUID target) {
		this.packageID = entry.getId();
		this.user = user;
		this.target = target;
		this.timestamp = System.currentTimeMillis();

		try {
			save();
		} catch (DatabaseSaveException exception) {
			Logger.error(exception);
		}

		Logger.info(
				new MessageBuilder("User {user} has purchased package {packageID} for {target}.")
						.parse("user", this.user.toString())
						.parse("packageID", this.packageID)
						.parse("target", this.target.toString())
		);
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
