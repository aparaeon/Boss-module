package gg.mmorealms.module.wild.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.wild.common.WildCommonModule;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Getter
public abstract class WildBackendModule extends WildCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static WildBackendModule instance;

	private @Inject FileManager fileManager;

	private WildConfig config;

	public WildBackendModule() {
		WildBackendModule.instance = this;
	}

	@Override
	@SneakyThrows
	public void onInit() {
		this.config = export(fileManager.load(WildConfig.class));
	}

	@Override
	public void onEnable() {

	}
}
