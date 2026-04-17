package gg.mmorealms.module.modpack_rewards.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerJoinEvent;
import gg.mmorealms.module.modpack_rewards.backend.common.ModpackRewardsBackendModule;

import java.util.UUID;

public class Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		UUID uuid = event.getPlayer().getUUID();
		ModpackRewardsBackendModule.instance().getPacketManager().addToCheck(uuid);

		ScheduleUtils.runTaskLater(() -> {
			ModpackRewardsBackendModule.instance().getPacketManager().check(uuid);
		}, Time.seconds(30));
	}

}
