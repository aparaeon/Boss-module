package gg.mmorealms.module.gyms.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.gyms.backend.common.dto.database.IUserGymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.database.RemoteUserGymRecord;
import gg.mmorealms.module.gyms.backend.common.dto.database.UserGymRecord;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UserGymRecordLoader extends BackendPlayerDependentDatabaseLoader<IUserGymRecord, UserGymRecord, RemoteUserGymRecord> {

	public UserGymRecordLoader() {
		super(IUserGymRecord.class, UserGymRecord.class, RemoteUserGymRecord.class);
	}

	@Override
	protected @NotNull RemoteUserGymRecord createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
		return new RemoteUserGymRecord(uuid, server);
	}

	@Override
	protected @Nullable UserGymRecord createObject(@NotNull UUID uuid) {
		return new UserGymRecord(uuid);
	}


	@Override
	public void onJoin(@NotNull ServerPlayer player) {
	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {
	}
}