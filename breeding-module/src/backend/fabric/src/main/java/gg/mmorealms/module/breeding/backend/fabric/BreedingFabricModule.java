package gg.mmorealms.module.breeding.backend.fabric;

import com.raduvoinea.commandmanager.backend.fabric.FabricMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.breeding.backend.common.BreedingBackendModule;
import gg.mmorealms.module.breeding.backend.fabric.config.BreedingConfig;
import gg.mmorealms.module.breeding.backend.fabric.manager.PlayerEvents;
import gg.mmorealms.module.breeding.backend.fabric.registry.BreedingRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

@Getter
public class BreedingFabricModule extends BreedingBackendModule implements ModInitializer {

    @Getter
    @Accessors(fluent = true)
    private static BreedingFabricModule instance;

    private @Inject MinecraftServer server;
    private @Inject FileManager fileManager;
    private @Inject FabricMiniMessageManager miniMessageManager;

    private ServerType serverType;
    private BreedingConfig config; // exported

    public BreedingFabricModule() {
        BreedingFabricModule.instance = this;
    }

    @Override
    public void onInit() {
        this.config = export(fileManager.load(BreedingConfig.class));
        this.serverType = BackendLoader.instance().getServerType();

        PlayerEvents.register();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onInitialize() {
        PolymerResourcePackUtils.addModAssets("mmorealms");
        BreedingRegistry.register();

        this.setup();
    }
}
