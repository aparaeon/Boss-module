package gg.mmorealms.module.login_rewards.velocity.manager.loader;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.velocity.manager.VelocityPlayerDependentDatabaseLoader;
import gg.mmorealms.module.login_rewards.velocity.dto.UserDailyLoginData;
import org.jetbrains.annotations.NotNull;

public class UserDailyLoader extends VelocityPlayerDependentDatabaseLoader<UserDailyLoginData> {

	public UserDailyLoader() {
		super(UserDailyLoginData.class);
	}

	@Override
	public void onJoin(@NotNull Player player) {
		UserDailyLoginData.getByPlayer(player);
	}

	@Override
	public void onLeave(@NotNull Player player) {
	}
}
