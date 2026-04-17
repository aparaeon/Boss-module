package gg.mmorealms.module.example.backend.fabric;

import gg.mmorealms.module.example.backend.common.ExampleBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class ExampleFabricModule extends ExampleBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static ExampleFabricModule instance;

	public ExampleFabricModule() {
		ExampleFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}
