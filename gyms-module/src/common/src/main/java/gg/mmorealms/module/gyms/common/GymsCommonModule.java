package gg.mmorealms.module.gyms.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.gyms.GymsModuleBuildConstants;

@Module(
		id = GymsModuleBuildConstants.ID,
		version = GymsModuleBuildConstants.VERSION,
		dependencies = GymsModuleBuildConstants.DEPENDENCIES,
		authors = {"Andrei-Madalin Coman"}
)
public abstract class GymsCommonModule implements CommonModule {
}