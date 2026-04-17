package gg.mmorealms.module.limbo.velocity;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.limbo.LimboModuleBuildConstants;
import gg.mmorealms.module.limbo.common.LimboCommonModule;
import gg.mmorealms.module.limbo.velocity.files.LimboConfig;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = LimboModuleBuildConstants.ID,
		name = LimboModuleBuildConstants.ID,
		version = LimboModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class LimboVelocityModule extends LimboCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static LimboVelocityModule instance;

	private @Inject FileManager fileManager;

	private LimboConfig config;

	public LimboVelocityModule() {
		LimboVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(this.fileManager.load(LimboConfig.class));
	}

	@Override
	public void onEnable() {
	}
}
