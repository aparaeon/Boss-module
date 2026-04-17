package gg.mmorealms.module.moderation.backend.fabric;

import gg.mmorealms.module.moderation.backend.common.ModerationBackendModule;
import net.fabricmc.api.ModInitializer;

public class ModerationFabricModule extends ModerationBackendModule implements ModInitializer {
	@Override
	public void onInitialize() {
		this.setup();
	}
}
