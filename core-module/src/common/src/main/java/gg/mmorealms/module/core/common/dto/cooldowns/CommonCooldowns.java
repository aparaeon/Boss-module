package gg.mmorealms.module.core.common.dto.cooldowns;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("FieldMayBeFinal")
@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class CommonCooldowns implements gg.mmorealms.module.core.common.dto.cooldowns.ICommonCooldowns, IDatabaseEntry<UUID> {

	@Id
	@jakarta.validation.constraints.NotNull
	private UUID uuid;

	@JdbcTypeCode(SqlTypes.JSON)
	private HashMap<String, Long> data = new HashMap<>(); // type, timestamp

	public CommonCooldowns(@NotNull UUID uuid) {
		this.uuid = uuid;

		getLoader().cache(uuid, this);
	}

	@Override
	public Long get(@NotNull String id) {
		return data.getOrDefault(id, 0L);
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@Override
	public void set(String type, long time) {
		if (time == -1) {
			data.put(type, Long.MAX_VALUE);
			return;
		}

		if (time <= 0) {
			data.remove(type);
			return;
		}

		data.put(type, System.currentTimeMillis() + time);
	}
}
