package gg.mmorealms.module.nop.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.nop.NopModuleBuildConstants;
import gg.mmorealms.module.nop.common.NopComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = NopModuleBuildConstants.ID,
		name = NopModuleBuildConstants.ID,
		version = NopModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class NopVelocityModule extends NopComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static NopVelocityModule instance;

	public NopVelocityModule() {
		NopVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}