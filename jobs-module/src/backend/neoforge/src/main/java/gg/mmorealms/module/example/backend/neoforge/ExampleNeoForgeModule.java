package gg.mmorealms.module.example.backend.neoforge;

import gg.mmorealms.module.example.ExampleModuleBuildConstants;
import gg.mmorealms.module.example.backend.common.ExampleBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.neoforged.fml.common.Mod;

@Mod(ExampleModuleBuildConstants.ID)
public class ExampleNeoForgeModule extends ExampleBackendModule {

	@Getter
	@Accessors(fluent = true)
	protected static ExampleNeoForgeModule instance;

	public ExampleNeoForgeModule() {
		ExampleNeoForgeModule.instance = this;
	}

}
