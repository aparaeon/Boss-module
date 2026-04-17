package gg.mmorealms.module.scoreboard.backend.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerJoinEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.player.PlayerLeaveEvent;
import gg.mmorealms.loader.backend.common.dto.event.fabric.server.ServerTickEvent;
import gg.mmorealms.module.scoreboard.backend.common.ScoreboardBackendModule;

public class Listener {

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event){
		ScoreboardBackendModule.instance().getScoreboardManager().onJoin(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLeaveEvent(PlayerLeaveEvent event){
		ScoreboardBackendModule.instance().getScoreboardManager().onLeave(event.getPlayer());
	}

	@EventHandler
	public void onServerTickEvent(ServerTickEvent event){
		ScoreboardBackendModule.instance().getScoreboardManager().onTick();
	}
}
