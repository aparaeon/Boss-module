package gg.mmorealms.module.crates.backend.neoforge;

import gg.mmorealms.module.crates.CratesModuleBuildConstants;
import gg.mmorealms.module.crates.backend.common.CratesBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(CratesModuleBuildConstants.ID)
public class CratesNeoForgeModule extends CratesBackendModule {

	public CratesNeoForgeModule() {
		this.setup();
	}

}
