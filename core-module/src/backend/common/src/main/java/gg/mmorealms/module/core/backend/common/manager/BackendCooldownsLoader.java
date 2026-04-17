package gg.mmorealms.module.core.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.core.backend.common.dto.cooldown.BackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.cooldown.RemoteBackendCooldowns;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BackendCooldownsLoader extends BackendPlayerDependentDatabaseLoader<IBackendCooldowns, BackendCooldowns, RemoteBackendCooldowns> {

	public BackendCooldownsLoader() {
		super(IBackendCooldowns.class, BackendCooldowns.class, RemoteBackendCooldowns.class);
	}

	@Override
	public @NotNull RemoteBackendCooldowns createRemoteObject(@NotNull UUID playerUUID, @NotNull String server) {
		return new RemoteBackendCooldowns(playerUUID, server);
	}

	@Override
	protected @NotNull BackendCooldowns createObject(@NotNull UUID uuid) {
		return new BackendCooldowns(uuid);
	}

	@Override
	public void onJoin(@NotNull ServerPlayer player) {

	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {

	}
}
