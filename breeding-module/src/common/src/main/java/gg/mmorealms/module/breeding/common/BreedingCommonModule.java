package gg.mmorealms.module.breeding.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.breeding.BreedingModuleBuildConstants;

@Module(
        id = BreedingModuleBuildConstants.ID,
        version = BreedingModuleBuildConstants.VERSION,
        authors = {"Zero Delusions"},
        dependencies = BreedingModuleBuildConstants.DEPENDENCIES
)
public abstract class BreedingCommonModule implements CommonModule {
}
