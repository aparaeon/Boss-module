package gg.mmorealms.module.core_protect.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.core_protect.CoreProtectModuleBuildConstants;
import gg.mmorealms.module.core_protect.common.CoreProtectComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = CoreProtectModuleBuildConstants.ID,
		name = CoreProtectModuleBuildConstants.ID,
		version = CoreProtectModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"}
)
@Getter
public class CoreProtectVelocityModule extends CoreProtectComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static CoreProtectVelocityModule instance;

	public CoreProtectVelocityModule() {
		CoreProtectVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}