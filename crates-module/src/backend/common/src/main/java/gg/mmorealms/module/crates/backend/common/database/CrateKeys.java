package gg.mmorealms.module.crates.backend.common.database;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@NoArgsConstructor
@Entity(name = "crate_keys")
public class CrateKeys implements ICrateKeys, IDatabaseEntry<UUID> {

	@Id
	private UUID uuid;
	@JdbcTypeCode(SqlTypes.JSON)
	private ConcurrentHashMap<String, Integer> crateKeys = new ConcurrentHashMap<>();

	public CrateKeys(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getIdentifier() {
		return uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return CratesBackendModule.instance().getCrateKeysLoader();
	}

	@Override
	public Integer getKeys(String id) {
		return crateKeys.getOrDefault(id, 0);
	}

	@Override
	public synchronized void addKeys(String id, Integer amount) {
		int current = crateKeys.getOrDefault(id, 0);
		crateKeys.put(id, current + amount);
	}
}

