package gg.mmorealms.module.legendaries.velocity.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import gg.mmorealms.module.legendaries.common.dto.LegendaryInfo;
import gg.mmorealms.module.legendaries.common.dto.enums.LegendaryEventType;
import gg.mmorealms.module.legendaries.common.dto.event.LegendaryProxyEvent;
import gg.mmorealms.module.legendaries.velocity.LegendariesVelocityModule;

import java.util.UUID;

public class Listener {

	@EventHandler
	public void onLegendaryProxyEvent(LegendaryProxyEvent event) {
		var module = LegendariesVelocityModule.instance();
		var infoManager = module.getInfoManager();
		var lifecycleManager = module.getLifecycleManager();
		var messageManager = module.getMessageManager();

		LegendaryInfo info = event.getInfo();
		if (info == null) {
			return;
		}

		UUID pokemonUUID = info.getPokemonUUID();

        if (event.getType() == LegendaryEventType.SPAWNED) {
            lifecycleManager.scheduleDespawn(pokemonUUID);
            infoManager.add(info);
        } else if (event.getType() != LegendaryEventType.FAILED) {
            lifecycleManager.clearDespawnTask(pokemonUUID);
			infoManager.remove(pokemonUUID);
        }

		messageManager.sendMessage(info, event.getType());
	}

}

