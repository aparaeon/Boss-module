package gg.mmorealms.module.store.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.store.StoreModuleBuildConstants;
import gg.mmorealms.module.store.common.StoreCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = StoreModuleBuildConstants.ID,
		name = StoreModuleBuildConstants.ID,
		version = StoreModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class StoreVelocityModule extends StoreCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static StoreVelocityModule instance;

	public StoreVelocityModule() {
		StoreVelocityModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
