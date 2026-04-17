package gg.mmorealms.module.core.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import gg.mmorealms.loader.velocity.manager.VelocityPlayerDependentDatabaseLoader;
import gg.mmorealms.module.core.velocity.dto.cooldown.VelocityCooldowns;
import org.jetbrains.annotations.NotNull;

public class VelocityCooldownsLoader extends VelocityPlayerDependentDatabaseLoader<VelocityCooldowns> {

	public VelocityCooldownsLoader() {
		super(VelocityCooldowns.class);
	}

	@Override
	public void onJoin(@NotNull Player player) {

	}

	@Override
	public void onLeave(@NotNull Player player) {

	}
}
