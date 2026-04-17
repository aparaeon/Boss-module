package gg.mmorealms.module.legendaries.backend.fabric.manager.listener;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.loader.backend.common.annotation.OnlyOn;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.legendaries.backend.fabric.LegendariesModule;
import gg.mmorealms.module.legendaries.backend.fabric.manager.LegendaryInfoManager;
import gg.mmorealms.module.legendaries.common.dto.event.LegendaryDespawnEvent;
import gg.mmorealms.module.legendaries.common.dto.event.LegendarySpawnEvent;
import gg.mmorealms.module.legendaries.common.dto.request.LegendaryHeartbeatRequest;

@OnlyOn(servers = ServerType.WILD)
public class WildServerListener {

    @EventHandler
    public void onLegendaryHeartbeatRequest(LegendaryHeartbeatRequest request) {
        LegendaryInfoManager infoManager = LegendariesModule.instance().getInfoManager();
        request.setResult(new LegendaryHeartbeatRequest.Response(infoManager.getAll()));
    }

    @EventHandler
    public void onLegendarySpawnEvent(LegendarySpawnEvent request) {
        LegendariesModule
                .instance()
                .getSpawnManager()
                .attemptLegendarySpawn(request.getSenderUUID());
    }

    @EventHandler
    public void onLegendaryDespawnEvent(LegendaryDespawnEvent request) {
        LegendariesModule
                .instance()
                .getDespawnManager()
                .despawnLegendary(request.getPokemonUUID());
    }

}
