package gg.mmorealms.module.moderation.backend.neoforge;

import gg.mmorealms.module.moderation.ModerationModuleBuildConstants;
import gg.mmorealms.module.moderation.backend.common.ModerationBackendModule;
import net.neoforged.fml.common.Mod;

@Mod(ModerationModuleBuildConstants.ID)
public class ModerationNeoForgeModule extends ModerationBackendModule {
	public ModerationNeoForgeModule() {
		this.setup();
	}
}
