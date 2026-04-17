package gg.mmorealms.module.user_data.velocity.database;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.module.user_data.common.database.CommonUserSettings;
import gg.mmorealms.module.user_data.velocity.UserDataVelocityModule;
import gg.mmorealms.module.user_data.velocity.manager.VelocityUserSettingsLoader;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(name = "velocity_user_settings")
@Getter
@Setter
@NoArgsConstructor
public class VelocityUserSettings extends CommonUserSettings<Player> {

	public VelocityUserSettings(UUID uuid) {
		super(uuid);
	}

	public static @NotNull VelocityUserSettings getByUUID(UUID uuid) {
		VelocityUserSettings userSettings = UserDataVelocityModule.instance.getUserSettingsLoader().getByIdentifier(uuid);

		if (userSettings == null) {
			userSettings = new VelocityUserSettings(uuid);
			UserDataVelocityModule.instance.getUserSettingsLoader().cache(uuid, userSettings);
		}

		return userSettings;
	}

	@Override
	@Transient
	public VelocityUserSettingsLoader getLoader() {
		return UserDataVelocityModule.instance.getUserSettingsLoader();
	}
}
