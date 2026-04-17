package gg.mmorealms.module.analytics.velocity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.analytics.AnalyticsModuleBuildConstants;
import gg.mmorealms.module.analytics.common.AnalyticsCommonModule;
import gg.mmorealms.module.analytics.velocity.manager.AfkManager;
import gg.mmorealms.module.analytics.velocity.manager.PacketHandler;
import gg.mmorealms.module.analytics.velocity.manager.UserStatsLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = AnalyticsModuleBuildConstants.ID,
		name = AnalyticsModuleBuildConstants.ID,
		version = AnalyticsModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class AnalyticsVelocityModule extends AnalyticsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static AnalyticsVelocityModule instance;

	private UserStatsLoader userStatsLoader;
	private AfkManager afkManager;

	public AnalyticsVelocityModule() {
		AnalyticsVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.userStatsLoader = new UserStatsLoader();
		this.afkManager = new AfkManager();
	}

	@Override
	public void onEnable() {
		PacketEvents.getAPI().getEventManager().registerListener(new PacketHandler(), PacketListenerPriority.NORMAL);
	}
}
