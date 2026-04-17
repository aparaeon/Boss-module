package gg.mmorealms.module.resource_pack.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.resource_pack.ResourcePackModuleBuildConstants;
import gg.mmorealms.loader.common.annotation.Module;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
	id = ResourcePackModuleBuildConstants.ID,
	version = ResourcePackModuleBuildConstants.VERSION,
	authors = {"Radu Voinea"},
	dependencies = ResourcePackModuleBuildConstants.DEPENDENCIES
)
public abstract class ResourcePackComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static ResourcePackComonModule instance;

	public ResourcePackComonModule() {
		ResourcePackComonModule.instance = this;
	}
}