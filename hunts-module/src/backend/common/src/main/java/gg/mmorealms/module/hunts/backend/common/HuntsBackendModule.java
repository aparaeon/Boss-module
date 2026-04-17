package gg.mmorealms.module.hunts.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.hunts.backend.common.config.HuntsConfig;
import gg.mmorealms.module.hunts.backend.common.manager.HuntEvents;
import gg.mmorealms.module.hunts.backend.common.manager.HuntsDatabaseLoader;
import gg.mmorealms.module.hunts.common.HuntsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class HuntsBackendModule extends HuntsCommonModule implements BackendModule {

    @Getter
    @Accessors(fluent = true)
    private static HuntsBackendModule instance;

    private @Inject MinecraftServer server;
    private @Inject FileManager fileManager;
    private @Inject DatabaseManager databaseManager;

    private HuntsDatabaseLoader huntsDatabaseLoader;

    private HuntsConfig config; // exported

    public HuntsBackendModule() {
        HuntsBackendModule.instance = this;
    }

    @Override
    public void onInit() {
        this.config = export(fileManager.load(HuntsConfig.class));
        this.huntsDatabaseLoader = new HuntsDatabaseLoader();
        HuntEvents.register();
    }

    @Override
    public void onEnable() {

    }
}
