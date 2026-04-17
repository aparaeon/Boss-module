package gg.mmorealms.module.example.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.example.ExampleModuleBuildConstants;
import gg.mmorealms.module.example.common.ExampleComonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Plugin(
		id = ExampleModuleBuildConstants.ID,
		name = ExampleModuleBuildConstants.ID,
		version = ExampleModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
@Getter
public class ExampleVelocityModule extends ExampleComonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	protected static ExampleVelocityModule instance;

	public ExampleVelocityModule() {
		ExampleVelocityModule.instance = this;
	}

	@Override
	public void onInit() throws ModuleException {

	}

	@Override
	public void onEnable() throws ModuleException {

	}
}