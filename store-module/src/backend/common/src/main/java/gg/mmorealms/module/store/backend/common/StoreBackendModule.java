package gg.mmorealms.module.store.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.module.chat.backend.common.manager.BackendChatInputManager;
import gg.mmorealms.module.store.backend.common.files.StoreConfig;
import gg.mmorealms.module.store.common.StoreCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public abstract class StoreBackendModule extends StoreCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static StoreBackendModule instance;

	private @Inject FileManager fileManager;
	private @Inject BackendChatInputManager chatInputManager;
	private @Inject BackendMiniMessageManager miniMessageManager;

	private StoreConfig config;

	public StoreBackendModule() {
		StoreBackendModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = this.fileManager.load(StoreConfig.class);
		this.config.bake();
	}

	@Override
	public void onEnable() {

	}
}
