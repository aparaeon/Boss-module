package gg.mmorealms.module.store.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.store.StoreModuleBuildConstants;
import lombok.Getter;

@Getter
@Module(
		id = StoreModuleBuildConstants.ID,
		version = StoreModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = StoreModuleBuildConstants.DEPENDENCIES
)
public abstract class StoreCommonModule implements CommonModule {

	public StoreCommonModule() {
	}

}
