package gg.mmorealms.module.user_data.backend.common.database.user_settings;

import gg.mmorealms.loader.common.dto.remote.UUIDRemoteObject;
import gg.mmorealms.module.user_data.common.dto.IUserSetting;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoteBackendUserSettings extends UUIDRemoteObject<IBackendUserSettings> implements IBackendUserSettings {

	public RemoteBackendUserSettings(@NotNull UUID uuid, @NotNull String server) {
		super(IBackendUserSettings.class, uuid, server);
	}

	@Override
	public void save() {
		sendRequest();
	}

	public <T extends IUserSetting<ServerPlayer>> T get(Class<T> clazz) {
		return sendRequest(clazz);
	}
}
