package gg.mmorealms.module.crates.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.crates.CratesModuleBuildConstants;
import gg.mmorealms.module.crates.common.CratesCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter

@Plugin(
		id = CratesModuleBuildConstants.ID,
		name = CratesModuleBuildConstants.ID,
		version = CratesModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"}
)
public class CratesModule extends CratesCommonModule implements VelocityModule {

	@Getter
	@Accessors(fluent = true)
	private static CratesModule instance;

	public CratesModule() {
		CratesModule.instance = this;
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onEnable() {
	}
}
