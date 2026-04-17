package gg.mmorealms.module.warps.velocity;

import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.warps.WarpsModuleBuildConstants;
import gg.mmorealms.module.warps.common.WarpsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = WarpsModuleBuildConstants.ID,
		name = WarpsModuleBuildConstants.ID,
		version = WarpsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"}
)
public class WarpsVelocityModule extends WarpsCommonModule implements VelocityModule {
	@Getter
	@Accessors(fluent = true)
	private static WarpsVelocityModule instance;

	private @Inject FileManager fileManager;
	private @Inject VelocityMiniMessageManager miniMessageManager;
	private @Inject ProxyServer proxy;


	public WarpsVelocityModule() {
		WarpsVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
