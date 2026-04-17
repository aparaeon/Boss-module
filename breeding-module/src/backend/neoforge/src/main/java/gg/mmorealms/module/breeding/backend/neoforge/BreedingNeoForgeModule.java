package gg.mmorealms.module.breeding.backend.neoforge;

import gg.mmorealms.module.breeding.BreedingModuleBuildConstants;
import gg.mmorealms.module.breeding.backend.common.BreedingBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(BreedingModuleBuildConstants.ID)
public class BreedingNeoForgeModule extends BreedingBackendModule {
    public BreedingNeoForgeModule() {
        this.setup();
    }
}
