package gg.mmorealms.module.user_data.backend.common.database.user_settings;

import gg.mmorealms.module.user_data.backend.common.manager.BackendUserSettingsLoader;
import gg.mmorealms.module.user_data.common.database.CommonUserSettings;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

@Entity(name = "backend_user_settings")
@Getter
@Setter
@NoArgsConstructor
public class BackendUserSettings extends CommonUserSettings<ServerPlayer> implements IBackendUserSettings {

	public BackendUserSettings(UUID uuid) {
		super(uuid);
	}

	@Override
	@Transient
	public BackendUserSettingsLoader getLoader() {
		return null;
	}

}
