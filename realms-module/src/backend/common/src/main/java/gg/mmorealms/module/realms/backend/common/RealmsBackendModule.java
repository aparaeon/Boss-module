package gg.mmorealms.module.realms.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import gg.mmorealms.module.core.backend.common.manager.EngineManager;
import gg.mmorealms.module.realms.backend.common.config.RealmsConfig;
import gg.mmorealms.module.realms.backend.common.config.S3Config;
import gg.mmorealms.module.realms.backend.common.manager.RealmsLoader;
import gg.mmorealms.module.realms.backend.common.manager.RealmsManager;
import gg.mmorealms.module.realms.backend.common.manager.S3Manager;
import gg.mmorealms.module.realms.common.RealmsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

import java.util.List;

@Getter
public abstract class RealmsBackendModule extends RealmsCommonModule implements BackendModule {
	// Static
	@Accessors(fluent = true)
	@Getter
	private static RealmsBackendModule instance;

	private @Inject EngineManager engineManager;
	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;

	private @Inject BackendMiniMessageManager miniMessageManager;

	private S3Manager s3Manager;
	private RealmsLoader realmsLoader; // exported
	private RealmsManager realmsManager; // exported
	private RealmsConfig config; // exported

	public RealmsBackendModule() {
		RealmsBackendModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(RealmsConfig.class));

		this.realmsLoader = export(new RealmsLoader());
		this.realmsManager = export(new RealmsManager());

		this.onlyOn(ServerType.REALMS, () -> {
			int s3ConfigCount = 1;
			String s3ConfigCountString = SecretsUtils.getEnvironmentVariable("S3_CONFIG_COUNT");
			if (s3ConfigCountString != null && !s3ConfigCountString.isEmpty()) {
				s3ConfigCount = Integer.parseInt(s3ConfigCountString);
			}

			List<S3Config> s3Configs = SecretsUtils.loadSecretsConfigs(S3Config.class, s3ConfigCount);

			this.s3Manager = new S3Manager(s3Configs);
		});
	}

	@Override
	public void onEnable() {
	}
}