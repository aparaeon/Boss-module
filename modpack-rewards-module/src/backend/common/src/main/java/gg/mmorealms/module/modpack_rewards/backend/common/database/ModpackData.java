package gg.mmorealms.module.modpack_rewards.backend.common.database;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.UUID;

@Entity(name = "modpack_data")
@NoArgsConstructor
@Getter
public class ModpackData implements IDatabaseEntry<UUID> {
	@Id
	@NotNull
	private UUID uuid;

	private String modpack = "";

	public ModpackData(@NotNull UUID uuid) {
		this.uuid = uuid;

		try {
			save();
		} catch (DatabaseSaveException e) {
			Logger.error(e);
		}
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public DatabaseLoader<UUID, ?, ?> getLoader() {
		return null;
	}

	public static ModpackData getByUUID(UUID uuid) {
		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			ModpackData modpackData = session.get(ModpackData.class, uuid);

			if (modpackData == null) {
				modpackData = new ModpackData(uuid);
			}

			return modpackData;
		}
	}

	public void setModpack(String modpack) {
		this.modpack = modpack;
		try {
			save();
		} catch (DatabaseSaveException e) {
			Logger.error(e);
		}
	}

	public boolean hasModpack() {
		return this.modpack != null && !this.modpack.isEmpty();
	}

}
