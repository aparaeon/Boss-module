package gg.mmorealms.module.breeding.velocity;

import com.velocitypowered.api.plugin.Plugin;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.velocity.dto.VelocityModule;
import gg.mmorealms.module.breeding.BreedingModuleBuildConstants;
import gg.mmorealms.module.breeding.common.BreedingCommonModule;

@Plugin(
        id = BreedingModuleBuildConstants.ID,
        name = BreedingModuleBuildConstants.ID,
        version = BreedingModuleBuildConstants.VERSION,
        authors = {"ZeroDelusions"}
)
public class BreedingVelocityModule extends BreedingCommonModule implements VelocityModule {

    @Override
    public void onInit() throws ModuleException {

    }

    @Override
    public void onEnable() throws ModuleException {

    }

}
