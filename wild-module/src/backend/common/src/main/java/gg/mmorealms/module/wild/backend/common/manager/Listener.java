package gg.mmorealms.module.wild.backend.common.manager;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.generic.dto.Range;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.dto.location.Location;
import gg.mmorealms.module.core.backend.common.dto.LevelType;
import gg.mmorealms.module.core.backend.common.dto.cooldown.IBackendCooldowns;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.utils.LocationUtils;
import gg.mmorealms.module.wild.backend.common.WildConfig;
import gg.mmorealms.module.wild.backend.common.dto.event.TeleportToRandomLocationEvent;
import net.minecraft.server.MinecraftServer;

@OnlyOn(servers = ServerType.WILD)
public class Listener {

	private final static int MAX_ATTEMPTS = 10;

	private @Inject MinecraftServer server;
	private @Inject WildConfig config;

	@EventHandler
	private void onTeleportToRandomLocationEvent(TeleportToRandomLocationEvent event) {
		IUser user = IUser.getByUUID(event.getUuid());
		Location wildLocation;

		int iter = 0;
		do {
			wildLocation = getRadomLocation(event.getLevelType());
			if (iter++ == MAX_ATTEMPTS) {
				Logger.debug("Was unable to find spawn location");

				IBackendCooldowns cooldowns = IBackendCooldowns.getByUser(user);
				String cooldownName = WildConfig.RTP_COOLDOWN_TEMPLATE.parse("dimension", event.getLevelType().getFriendlyNames().getFirst()).parse();
				cooldowns.remove(cooldownName);

				user.kick("Was unable to find spawn location");
				return;
			}

		} while (!LocationUtils.setLocationToGround(wildLocation, config.spawnDenyList));

		user.teleport(wildLocation);
	}

	private Location getRadomLocation(LevelType world) {
		return LocationUtils.getRandomLocation(
				// TODO: Maybe modify Location `world` field to be of type LevelType
				world.getResourceKey().location().getPath(),
				config.spawnRange.first(),
				Range.exact(255),
				config.spawnRange.second()
		);
	}

}