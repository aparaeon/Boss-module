package gg.mmorealms.module.voting.backend.fabric;

import gg.mmorealms.module.voting.backend.common.VotingBackendModule;
import net.fabricmc.api.ModInitializer;

public class VotingFabricModule extends VotingBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
