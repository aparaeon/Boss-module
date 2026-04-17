package gg.mmorealms.module.core_protect.backend.fabric;

import gg.mmorealms.module.core_protect.backend.common.CoreProtectBackendModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;

public class CoreProtectFabricModule extends CoreProtectBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	protected static CoreProtectFabricModule instance;

	public CoreProtectFabricModule() {
		CoreProtectFabricModule.instance = this;
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}