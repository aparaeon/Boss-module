package gg.mmorealms.module.user_data.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.velocity.manager.VelocityPlayerDependentDatabaseLoader;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import gg.mmorealms.module.user_data.velocity.database.VelocityUserSettings;
import org.jetbrains.annotations.NotNull;

public class VelocityUserSettingsLoader extends VelocityPlayerDependentDatabaseLoader<VelocityUserSettings> {
	public VelocityUserSettingsLoader() {
		super(VelocityUserSettings.class);
	}

	@Override
	public void onJoin(@NotNull Player player) {
		VelocityUserSettings userSettings = VelocityUserSettings.getByUUID(player.getUniqueId());

		for (IUserSetting<Player> setting : userSettings.getSettings()) {
			setting.apply(player);
		}
	}

	@Override
	public void onLeave(@NotNull Player player) {

	}
}
