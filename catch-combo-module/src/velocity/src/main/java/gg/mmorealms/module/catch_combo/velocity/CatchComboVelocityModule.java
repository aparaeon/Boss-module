package gg.mmorealms.module.catch_combo.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.catch_combo.CatchComboModuleBuildConstants;
import gg.mmorealms.module.catch_combo.common.CatchComboComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = CatchComboModuleBuildConstants.ID,
		name = CatchComboModuleBuildConstants.ID,
		version = CatchComboModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"}
)
@Getter
public class CatchComboVelocityModule extends CatchComboComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static CatchComboVelocityModule instance;

	public CatchComboVelocityModule() {
		CatchComboVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}