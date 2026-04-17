package gg.mmorealms.module.core.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.dto.location.UUIDLocation;
import gg.mmorealms.module.core.backend.common.config.CoreConfig;
import gg.mmorealms.module.core.backend.common.dto.BackendDetails;
import gg.mmorealms.module.core.backend.common.dto.user.IUser;
import gg.mmorealms.module.core.backend.common.dto.user.User;
import gg.mmorealms.module.core.backend.common.manager.BackendCooldownsLoader;
import gg.mmorealms.module.core.backend.common.manager.BackendGUIManager;
import gg.mmorealms.module.core.backend.common.manager.EngineManager;
import gg.mmorealms.module.core.backend.common.manager.UserLoader;
import gg.mmorealms.module.core.backend.common.world.ModFeatures;
import gg.mmorealms.module.core.common.CoreCommonModule;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class CoreBackendModule extends CoreCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static CoreBackendModule instance;

	private @Inject MinecraftServer server;
	private @Inject RegistryAccess registryAccess;
	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private EngineManager engineManager; // exported
	private CoreConfig config; // Exported
	private BackendDetails backendDetails; // Exported
	private UserLoader userLoader;
	private BackendCooldownsLoader cooldownsLoader;
	private BackendGUIManager guiManager;
	private @Setter boolean shuttingDown = false;

	public CoreBackendModule(BackendGUIManager guiManager) {
		CoreBackendModule.instance = this;
		this.guiManager = guiManager;

		ModFeatures.register();
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(CoreConfig.class));
		this.init(this.config);

		UUIDLocation.LOCATION_FETCH = (uuid -> {
			User user = User.unsafeGetByUUIDOrThrow(uuid);
			return user.getBlockLocation();
		});

		this.backendDetails = export(new BackendDetails());
		this.engineManager = export(new EngineManager(this.backendDetails));
		this.userLoader = new UserLoader();
		this.cooldownsLoader = new BackendCooldownsLoader();
	}

	@Override
	public void onEnable() {

	}
}
