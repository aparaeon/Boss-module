package gg.mmorealms.module.moderation.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.velocity.manager.VelocityPlayerDependentDatabaseLoader;
import gg.mmorealms.module.moderation.velocity.database.UserPunishments;
import org.jetbrains.annotations.NotNull;

public class UserPunishmentsDatabaseLoader extends VelocityPlayerDependentDatabaseLoader<UserPunishments> {

	public UserPunishmentsDatabaseLoader() {
		super(UserPunishments.class);
	}

	@Override
	public void onJoin(@NotNull Player player) {

	}

	@Override
	public void onLeave(@NotNull Player player) {

	}
}
