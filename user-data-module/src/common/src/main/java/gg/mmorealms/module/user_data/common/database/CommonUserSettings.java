package gg.mmorealms.module.user_data.common.database;

import gg.mmorealms.loader.common.dto.database.IDatabaseEntry;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class CommonUserSettings<Player> implements IDatabaseEntry<UUID> {

	private static List<Class<? extends IUserSetting<?>>> REGISTERED_SETTINGS = new ArrayList<>();

	@Id
	@jakarta.validation.constraints.NotNull
	private @Getter UUID uuid;

	@JdbcTypeCode(SqlTypes.JSON)
	private List<IUserSetting<Player>> settings = new ArrayList<>();

	public CommonUserSettings(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getIdentifier() {
		return this.uuid;
	}

	@SneakyThrows
	public <T extends IUserSetting<Player>> T get(Class<T> clazz) {
		for (IUserSetting<Player> setting : settings) {
			if (setting.getClass().equals(clazz)) {
				return clazz.cast(setting);
			}
		}

		T newSetting = clazz.getConstructor().newInstance();
		settings.add(newSetting);
		return newSetting;
	}

}