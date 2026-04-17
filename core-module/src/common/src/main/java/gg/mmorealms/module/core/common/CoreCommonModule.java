package gg.mmorealms.module.core.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.loader.common.manager.ModuleManager;
import gg.mmorealms.module.core.CoreModuleBuildConstants;
import gg.mmorealms.module.core.common.files.CommonCoreConfig;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
		id = CoreModuleBuildConstants.ID,
		version = CoreModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = CoreModuleBuildConstants.DEPENDENCIES
)
public abstract class CoreCommonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	private static CoreCommonModule instance;

	private @Inject ModuleManager moduleManager;

	private CommonCoreConfig config;

	public CoreCommonModule() {
		CoreCommonModule.instance = this;
	}

	public void init(CommonCoreConfig config) {
		this.config = config;
	}

}
