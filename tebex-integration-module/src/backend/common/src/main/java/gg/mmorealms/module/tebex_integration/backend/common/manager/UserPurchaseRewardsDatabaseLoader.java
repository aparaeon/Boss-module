package gg.mmorealms.module.tebex_integration.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.tebex_integration.backend.common.dto.database.IUserPurchaseRewardsData;
import gg.mmorealms.module.tebex_integration.backend.common.dto.database.RemoteUserPurchaseRewardsData;
import gg.mmorealms.module.tebex_integration.backend.common.dto.database.UserPurchaseRewardsData;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UserPurchaseRewardsDatabaseLoader extends BackendPlayerDependentDatabaseLoader<
		IUserPurchaseRewardsData,
		UserPurchaseRewardsData,
		RemoteUserPurchaseRewardsData> {
	public UserPurchaseRewardsDatabaseLoader() {
		super(IUserPurchaseRewardsData.class, UserPurchaseRewardsData.class, RemoteUserPurchaseRewardsData.class);
	}

	@Override
	public void onJoin(@NotNull ServerPlayer player) {
		// Do not load on join
	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {

	}

	@Override
	protected @NotNull RemoteUserPurchaseRewardsData createRemoteObject(@NotNull UUID uuid, @NotNull String server) {
		return new RemoteUserPurchaseRewardsData(uuid, server);
	}

	@Override
	protected @Nullable UserPurchaseRewardsData createObject(@NotNull UUID uuid) {
		return new UserPurchaseRewardsData(uuid);
	}
}
