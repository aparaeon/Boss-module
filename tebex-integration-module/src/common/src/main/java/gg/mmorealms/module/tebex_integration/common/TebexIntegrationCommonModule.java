package gg.mmorealms.module.tebex_integration.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.tebex_integration.TebexIntegrationModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = TebexIntegrationModuleBuildConstants.ID,
		version = TebexIntegrationModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = TebexIntegrationModuleBuildConstants.DEPENDENCIES
)
public abstract class TebexIntegrationCommonModule implements CommonModule {

	public TebexIntegrationCommonModule() {
	}

}
