package gg.mmorealms.module.voting.backend.neoforge;

import gg.mmorealms.module.voting.VotingModuleBuildConstants;
import gg.mmorealms.module.voting.backend.common.VotingBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(VotingModuleBuildConstants.ID)
public class VotingNeoForgeModule extends VotingBackendModule {
	public VotingNeoForgeModule() {
		this.setup();
	}
}
