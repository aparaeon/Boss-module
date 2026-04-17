package gg.mmorealms.module.kits.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.kits.KitsModuleBuildConstants;
import gg.mmorealms.module.kits.common.KitsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Plugin(
		id = KitsModuleBuildConstants.ID,
		name = KitsModuleBuildConstants.ID,
		version = KitsModuleBuildConstants.VERSION,
		authors = {"Andrei-Madalin Coman"}
)
public class KitModule extends KitsCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static KitModule instance;

	public KitModule() {
		KitModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
