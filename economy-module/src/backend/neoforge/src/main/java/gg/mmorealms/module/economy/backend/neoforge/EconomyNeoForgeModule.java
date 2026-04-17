package gg.mmorealms.module.economy.backend.neoforge;

import gg.mmorealms.module.economy.EconomyModuleBuildConstants;
import gg.mmorealms.module.economy.backend.common.EconomyBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(EconomyModuleBuildConstants.ID)
public class EconomyNeoForgeModule extends EconomyBackendModule {

	public EconomyNeoForgeModule() {
		this.setup();
	}

}
