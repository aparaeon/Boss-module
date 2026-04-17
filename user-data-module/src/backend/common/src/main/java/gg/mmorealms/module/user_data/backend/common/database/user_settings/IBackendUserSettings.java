package gg.mmorealms.module.user_data.backend.common.database.user_settings;

import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.user_data.backend.common.UserDataBackendModule;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface IBackendUserSettings extends ISavable {

	static BackendUserSettings getByPlayer(ServerPlayer player) {
		IBackendUserSettings userSettings = getByUUID(player.getUUID());

		if (!(userSettings instanceof BackendUserSettings)) {
			throw new IllegalArgumentException("UserSettings not found for player: " + player.getUUID());
		}

		return (BackendUserSettings) userSettings;
	}

	static BackendUserSettings getByUser(User user) {
		IBackendUserSettings userSettings = getByUUID(user.getUUID());

		if (!(userSettings instanceof BackendUserSettings)) {
			throw new IllegalArgumentException("UserSettings not found for user: " + user.getUUID());
		}

		return (BackendUserSettings) userSettings;
	}

	static IBackendUserSettings getByUUID(UUID uuid) {
		return UserDataBackendModule.instance().getBackendUserSettingsLoader().getByIdentifier(uuid);
	}

	<T extends IUserSetting<ServerPlayer>> T get(Class<T> clazz);

}
