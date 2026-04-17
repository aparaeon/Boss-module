package gg.mmorealms.module.legendaries.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.core.velocity.manager.ServerManager;
import gg.mmorealms.module.legendaries.LegendariesModuleBuildConstants;
import gg.mmorealms.module.legendaries.common.LegendariesCommonModule;
import gg.mmorealms.module.legendaries.velocity.config.LegendarySpawnConfig;
import gg.mmorealms.module.legendaries.velocity.manager.LegendaryInfoManager;
import gg.mmorealms.module.legendaries.velocity.manager.LegendaryLifecycleManager;
import gg.mmorealms.module.legendaries.velocity.manager.LegendaryMessageManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = LegendariesModuleBuildConstants.ID,
		name = LegendariesModuleBuildConstants.ID,
		version = LegendariesModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"}
)
public class LegendariesVelocityModule extends LegendariesCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static LegendariesVelocityModule instance;

	private @Inject ProxyServer proxy;
	private @Inject ServerManager serverManager;
	private @Inject FileManager fileManager;
	private @Inject RedisManager redisManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;

	private LegendarySpawnConfig config; // exported
	private LegendaryInfoManager infoManager; // exported
	private LegendaryMessageManager messageManager;
	private LegendaryLifecycleManager lifecycleManager; // exported

	public LegendariesVelocityModule() {
		LegendariesVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(LegendarySpawnConfig.class));
		this.infoManager = export(new LegendaryInfoManager());

		this.messageManager = new LegendaryMessageManager(this.proxy, this.miniMessageManager);
		this.lifecycleManager = export(new LegendaryLifecycleManager(this.infoManager));
	}

	@Override
	public void onEnable() {
		this.lifecycleManager.start();
	}

}
