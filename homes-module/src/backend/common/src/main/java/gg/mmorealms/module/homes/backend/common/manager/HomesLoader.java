package gg.mmorealms.module.homes.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.homes.backend.common.dto.Homes;
import gg.mmorealms.module.homes.backend.common.dto.IHomes;
import gg.mmorealms.module.homes.backend.common.dto.RemoteHomes;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HomesLoader extends BackendPlayerDependentDatabaseLoader<IHomes, Homes, RemoteHomes> {
	public HomesLoader() {
		super(IHomes.class, Homes.class, RemoteHomes.class);
	}

	@Override
	protected @NotNull RemoteHomes createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
		return new RemoteHomes(uuid, server);
	}

	@Override
	protected @Nullable Homes createObject(@NotNull UUID uuid) {
		return new Homes(uuid);
	}

	@Override
	public void onJoin(@NotNull ServerPlayer player) {

	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {

	}
}
