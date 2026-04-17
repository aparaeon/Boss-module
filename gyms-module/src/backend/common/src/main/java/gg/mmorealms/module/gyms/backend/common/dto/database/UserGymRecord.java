package gg.mmorealms.module.gyms.backend.common.dto.database;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.gyms.backend.common.GymsBackendModule;
import gg.mmorealms.module.gyms.backend.common.manager.UserGymRecordLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@Entity(name = "user_gym_records")
@NoArgsConstructor
@Getter
public class UserGymRecord implements IDatabaseEntry<UUID>, IUserGymRecord {
	@Id
	@NotNull
	protected UUID uuid;
	@JdbcTypeCode(SqlTypes.JSON)
	protected HashMap<String, GymRecord> records = new HashMap<>();

	public UserGymRecord(@NotNull UUID uuid) {
		this.uuid = uuid;

		getLoader().cache(uuid, this);
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public UserGymRecordLoader getLoader() {
		return GymsBackendModule.instance().getGymRecordLoader();
	}

	@Override
	public void set(String id, GymRecord gymRecord) {
		this.records.put(id, gymRecord);
	}

	@Override
	public @Nullable GymRecord getGymRecord(String id) {
		return this.records.get(id);
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public void delete() {
		IDatabaseEntry.super.delete();
	}
}