package gg.mmorealms.module.plushies.backend.neoforge;

import gg.mmorealms.module.plushies.PlushiesModuleBuildConstants;
import gg.mmorealms.module.plushies.backend.common.PlushiesBackendModule;
import gg.mmorealms.module.plushies.backend.common.manager.IPlushiesPlatformImplementation;
import gg.mmorealms.module.plushies.backend.neoforge.manager.PlushiesNeoForgePlatformImplementation;
import net.neoforged.fml.common.Mod;

@Mod(PlushiesModuleBuildConstants.ID)
public class PlushiesNeoForgeModule extends PlushiesBackendModule {

	public PlushiesNeoForgeModule() {
		this.setup();
	}

	@Override
	protected IPlushiesPlatformImplementation createPlatformImplementation() {
		return new PlushiesNeoForgePlatformImplementation();
	}
}
