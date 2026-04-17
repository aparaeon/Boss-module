package gg.mmorealms.module.economy.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.economy.EconomyModuleBuildConstants;
import gg.mmorealms.module.economy.common.EconomyCommonModule;
import gg.mmorealms.module.economy.velocity.config.EconomyConfig;
import gg.mmorealms.module.economy.velocity.manager.TopManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = EconomyModuleBuildConstants.ID,
		name = EconomyModuleBuildConstants.ID,
		version = EconomyModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman", "Radu Voinea"}
)
public class EconomyVelocityModule extends EconomyCommonModule implements VelocityModule {
	@Getter
	@Accessors(fluent = true)
	private static EconomyVelocityModule instance;

	private @Inject FileManager fileManager;

	private EconomyConfig config; // exported
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ProxyServer proxy;

	private TopManager topManager;

	public EconomyVelocityModule() {
		EconomyVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(EconomyConfig.class));

		this.topManager = new TopManager();
	}

	@Override
	public void onEnable() {
		topManager.schedule();
	}
}
