package gg.mmorealms.module.nop.backend.fabric;

import gg.mmorealms.module.nop.backend.common.NopBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class NopFabricModule extends NopBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static NopFabricModule instance;

	public NopFabricModule() {
		NopFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}