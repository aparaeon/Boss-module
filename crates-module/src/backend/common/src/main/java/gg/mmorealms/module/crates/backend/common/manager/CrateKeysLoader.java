package gg.mmorealms.module.crates.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.crates.backend.common.database.CrateKeys;
import gg.mmorealms.module.crates.backend.common.database.ICrateKeys;
import gg.mmorealms.module.crates.backend.common.database.RemoteCrateKeys;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CrateKeysLoader extends BackendPlayerDependentDatabaseLoader<ICrateKeys, CrateKeys, RemoteCrateKeys> {
	public CrateKeysLoader() {
		super(ICrateKeys.class, CrateKeys.class, RemoteCrateKeys.class);
	}

	@Override
	public void onJoin(@NotNull ServerPlayer player) {

	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {

	}

	@Override
	protected @NotNull RemoteCrateKeys createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
		return new RemoteCrateKeys(uuid, server);
	}

	@Override
	protected @Nullable CrateKeys createObject(@NotNull UUID uuid) {
		return new CrateKeys(uuid);
	}
}
