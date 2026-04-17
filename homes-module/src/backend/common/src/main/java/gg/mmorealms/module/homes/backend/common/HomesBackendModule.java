package gg.mmorealms.module.homes.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.core.backend.common.manager.EngineManager;
import gg.mmorealms.module.homes.backend.common.config.HomesConfig;
import gg.mmorealms.module.homes.backend.common.manager.HomesLoader;
import gg.mmorealms.module.homes.common.HomesCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Getter
public abstract class HomesBackendModule extends HomesCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static HomesBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject EngineManager engineManager;

	private HomesConfig config; // exported
	private HomesLoader homesLoader;

	public HomesBackendModule() {
		HomesBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(HomesConfig.class));

		this.homesLoader = new HomesLoader();
	}

	@Override
	public void onEnable() {
	}

}
