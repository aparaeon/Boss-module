package gg.mmorealms.module.analytics.velocity.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import gg.mmorealms.module.analytics.common.dto.event.UserSpentAmountsRequest;
import gg.mmorealms.module.analytics.velocity.AnalyticsVelocityModule;
import gg.mmorealms.module.analytics.velocity.dto.UserStats;

public class Listener {

	@EventHandler
	public void onUserSpentAmountsRequest(UserSpentAmountsRequest event) {
		UserStats stats = UserStats.getByUUID(event.getUuid());

		if (stats == null) {
			event.setResult(new UserSpentAmountsRequest.Response(0, 0));
			return;
		}

		event.setResult(new UserSpentAmountsRequest.Response(
				stats.getPurchaseTotalMonthly(0),
				stats.getPurchasesTotal(0, System.currentTimeMillis())
		));
	}

	@EventHandler
	public void onUserSpentAmountsRequest(DisconnectEvent event) {
		AnalyticsVelocityModule.instance().getAfkManager().onLeave(event.getPlayer().getUniqueId());
	}


}
