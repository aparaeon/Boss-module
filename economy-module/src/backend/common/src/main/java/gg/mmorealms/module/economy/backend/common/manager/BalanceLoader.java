package gg.mmorealms.module.economy.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.economy.backend.common.dto.Balances;
import gg.mmorealms.module.economy.backend.common.dto.IBalances;
import gg.mmorealms.module.economy.backend.common.dto.RemoteBalances;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BalanceLoader extends BackendPlayerDependentDatabaseLoader<IBalances, Balances, RemoteBalances> {

	public BalanceLoader() {
		super(IBalances.class, Balances.class, RemoteBalances.class);
	}

	@Override
	protected @NotNull RemoteBalances createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
		return new RemoteBalances(uuid, server);
	}

	@Override
	protected @Nullable Balances createObject(@NotNull UUID uuid) {
		return new Balances(uuid);
	}


	@Override
	public void onJoin(@NotNull ServerPlayer player) {

	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {

	}
}
