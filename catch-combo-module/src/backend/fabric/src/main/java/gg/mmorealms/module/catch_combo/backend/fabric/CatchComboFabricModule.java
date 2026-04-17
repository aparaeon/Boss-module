package gg.mmorealms.module.catch_combo.backend.fabric;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.catch_combo.backend.common.CatchComboBackendModule;
import gg.mmorealms.module.catch_combo.backend.fabric.config.CatchComboConfig;
import gg.mmorealms.module.catch_combo.backend.fabric.manager.CatchComboDatabaseLoader;
import gg.mmorealms.module.catch_combo.backend.fabric.manager.CobblemonEventsListener;
import gg.mmorealms.module.catch_combo.backend.fabric.registry.CatchComboSpawningInfluenceRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;


@Getter
public class CatchComboFabricModule extends CatchComboBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	private static CatchComboFabricModule instance;

	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;
	private @Inject DatabaseManager databaseManager;

    private CatchComboDatabaseLoader catchComboDatabaseLoader;

	private ServerType serverType;
	private CatchComboConfig config; // exported

	public CatchComboFabricModule() {
		CatchComboFabricModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(CatchComboConfig.class));
		this.serverType = BackendLoader.instance().getServerType();
        this.catchComboDatabaseLoader = new CatchComboDatabaseLoader();
	}

	@Override
	public void onEnable() {
		CobblemonEventsListener.register();
        CatchComboSpawningInfluenceRegistry.register();
	}

	@Override
	public void onInitialize() {
		this.setup();
	}
}