package gg.mmorealms.module.example.common;

import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.example.ExampleModuleBuildConstants;
import gg.mmorealms.loader.common.annotation.Module;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Module(
	id = ExampleModuleBuildConstants.ID,
	version = ExampleModuleBuildConstants.VERSION,
	authors = {"Radu Voinea"},
	dependencies = ExampleModuleBuildConstants.DEPENDENCIES
)
public abstract class ExampleComonModule implements CommonModule {

	@Getter
	@Accessors(fluent = true)
	protected static ExampleComonModule instance;

	public ExampleComonModule() {
		ExampleComonModule.instance = this;
	}
}
