package gg.mmorealms.module.plushies.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.module.plushies.backend.common.config.PlushiesConfig;
import gg.mmorealms.module.plushies.backend.common.manager.IPlushiesPlatformImplementation;
import gg.mmorealms.module.plushies.common.PlushiesCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class PlushiesBackendModule extends PlushiesCommonModule implements BackendModule {

	public static final String PLUSHIE_TAG = "mmorealms:plushie";

	@Getter
	@Accessors(fluent = true)
	private static PlushiesBackendModule instance;

	private @Inject RegistryAccess registryAccess;
	private @Inject MinecraftServer server;
	private @Inject BackendMiniMessageManager miniMessageManager;
	private @Inject FileManager fileManager;

	private IPlushiesPlatformImplementation platformImplementation;

	private PlushiesConfig config; // exported

	public PlushiesBackendModule() {
		PlushiesBackendModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(PlushiesConfig.class));

		this.platformImplementation = createPlatformImplementation();
		export(this.platformImplementation);
		export(this.platformImplementation, IPlushiesPlatformImplementation.class);
	}

	@Override
	public void onEnable() {

	}

	protected abstract IPlushiesPlatformImplementation createPlatformImplementation();
}