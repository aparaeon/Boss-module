package gg.mmorealms.module.crates.backend.common.database;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ICrateKeys extends ISavable {

	static @NotNull ICrateKeys getByUser(IUser user) {
		return getByUUID(user.getUUID());
	}

	static @NotNull ICrateKeys getByUUID(UUID uuid) {
		ICrateKeys crateKeys = CratesBackendModule.instance().getCrateKeysLoader().getByIdentifier(uuid);

		if (crateKeys == null) {
			Logger.error("Failed to automatically create CrateKeys for user with UUID: " + uuid);
			return new CrateKeys(uuid);
		}

		return crateKeys;
	}

	Integer getKeys(String id);

	void addKeys(String id, Integer amount);

	default void removeKeys(String id, Integer amount) {
		addKeys(id, -amount);
	}

}
