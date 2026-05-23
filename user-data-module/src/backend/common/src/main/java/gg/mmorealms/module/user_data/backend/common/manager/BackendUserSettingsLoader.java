package gg.mmorealms.module.user_data.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.user_data.backend.common.database.user_settings.BackendUserSettings;
import gg.mmorealms.module.user_data.backend.common.database.user_settings.IBackendUserSettings;
import gg.mmorealms.module.user_data.backend.common.database.user_settings.RemoteBackendUserSettings;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BackendUserSettingsLoader extends BackendPlayerDependentDatabaseLoader<
		IBackendUserSettings,
		BackendUserSettings,
		RemoteBackendUserSettings
		> {
	public BackendUserSettingsLoader() {
		super(IBackendUserSettings.class, BackendUserSettings.class, RemoteBackendUserSettings.class);
	}

	@Override
	public void onJoin(@NotNull ServerPlayer player) {
		BackendUserSettings userSettings = IBackendUserSettings.getByPlayer(player);

		for (IUserSetting<ServerPlayer> setting : userSettings.getSettings()) {
			setting.apply(player);
		}
	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {
		BackendUserSettings userSettings = IBackendUserSettings.getByPlayer(player);

		for (IUserSetting<ServerPlayer> setting : userSettings.getSettings()) {
			setting.cleanup(player);
		}
	}

	@Override
	protected @NotNull RemoteBackendUserSettings createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
		return new RemoteBackendUserSettings(uuid, server);
	}

	@Override
	protected @Nullable BackendUserSettings createObject(@NotNull UUID uuid) {
		return new BackendUserSettings(uuid);
	}
}
