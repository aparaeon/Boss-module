package gg.mmorealms.module.moderation.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.moderation.common.ModerationCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public class ModerationBackendModule extends ModerationCommonModule implements BackendModule {

	// Static
	@Accessors(fluent = true)
	@Getter
	private static ModerationBackendModule instance;

	private @Inject MinecraftServer server;


	public ModerationBackendModule() {
		ModerationBackendModule.instance = this;
	}

	@Override
	public void onInit() {

	}

	@Override
	public void onEnable() {

	}
}