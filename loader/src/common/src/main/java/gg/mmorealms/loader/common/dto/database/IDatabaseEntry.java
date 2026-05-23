package gg.mmorealms.loader.common.dto.database;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;

public interface IDatabaseEntry<Identifier> extends ISavable {

	Identifier getIdentifier();

	@Override
	default void save() throws DatabaseSaveException {
		if (CommonLoader.DUMMY_MODE) {
			Logger.debug("Dummy mode enabled, skipping saving " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
			return;
		}

		Logger.debug("Saving " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());

		DatabaseManager.instance().getSessionFactory().inTransaction(transaction -> {
			if (this.getIdentifier() == null) {
				Logger.debug("Persisting " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
				transaction.persist(this);
			} else {
				Object existingEntity = transaction.get(this.getClass(), getIdentifier());
				if (existingEntity == null) {
					Logger.debug("Persisting " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
					transaction.persist(this);
				} else {
					Logger.debug("Merging " + this.getClass().getSimpleName() + " with identifier " + getIdentifier());
					transaction.merge(this);
				}
			}
		});
	}

	default void delete() {
		DatabaseLoader<Identifier, ?, ?> loader = getLoader();
		if (loader != null) {
			loader.clearCache(getIdentifier(), false);
		}

		DatabaseManager.instance().getSessionFactory().inTransaction(transaction -> transaction.remove(this));
	}

	DatabaseLoader<Identifier, ?, ?> getLoader();

}