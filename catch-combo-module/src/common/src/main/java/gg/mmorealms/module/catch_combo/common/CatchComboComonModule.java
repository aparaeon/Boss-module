package gg.mmorealms.module.catch_combo.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.catch_combo.CatchComboModuleBuildConstants;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
		id = CatchComboModuleBuildConstants.ID,
		version = CatchComboModuleBuildConstants.VERSION,
		authors = {"ZeroDelusions"},
		dependencies = CatchComboModuleBuildConstants.DEPENDENCIES
)
public abstract class CatchComboComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static CatchComboComonModule instance;

	public CatchComboComonModule() {
		CatchComboComonModule.instance = this;
	}
}