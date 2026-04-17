package gg.mmorealms.module.voting.common;

import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.module.voting.VotingModuleBuildConstants;

@Module(
		id = VotingModuleBuildConstants.ID,
		version = VotingModuleBuildConstants.VERSION,
		authors = {"Radu Voinea"},
		dependencies = VotingModuleBuildConstants.DEPENDENCIES
)
public abstract class VotingCommonModule implements CommonModule {
}
