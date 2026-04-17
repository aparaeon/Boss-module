package gg.mmorealms.module.core.backend.common.manager;

import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.module.core.backend.common.CoreBackendModule;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.RemoteUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import net.minecraft.server.level.ServerPlayer;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UserLoader extends BackendPlayerDependentDatabaseLoader<IUser, User, RemoteUser> {

	public UserLoader() {
		super(IUser.class, User.class, RemoteUser.class);
	}

	public @Nullable IUser getByUsername(@NotNull String username) {
		return getByIndexedFiled("username", username);
	}

	@SuppressWarnings("unused")
	public @Nullable UUID convertToUUID(@NotNull Session session, @NotNull String username) {
		UUID uuid = CoreBackendModule.instance().getEngineManager().getPlayersList().getUUID(username);

		if (uuid != null) {
			return uuid;
		}

		return session.createQuery("SELECT uuid FROM users WHERE username = :username", UUID.class)
				.setParameter("username", username)
				.getSingleResult();
	}

	public @NotNull RemoteUser createRemoteObject(@NotNull UUID playerUUID, @NotNull String server) {
		return new RemoteUser(playerUUID, server);
	}

	@Override
	protected @Nullable User createObject(@NotNull UUID uuid) {
		return new User(uuid);
	}

	@Override
	public void onJoin(@NotNull ServerPlayer player) {
		IUser user = IUser.getByUUID(player.getUUID());
		user.setUsername(player.getName().getString());
	}

	@Override
	public void onLeave(@NotNull ServerPlayer player) {

	}
}
