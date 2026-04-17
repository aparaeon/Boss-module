package gg.mmorealms.module.mega_evolution.backend.fabric;

import com.raduvoinea.commandmanager.backend.fabric.FabricMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.module.mega_evolution.backend.common.MegaEvolutionBackendModule;
import gg.mmorealms.module.mega_evolution.backend.fabric.config.MegaEvolutionConfig;
import gg.mmorealms.module.mega_evolution.backend.fabric.manager.CobblemonEventsListener;
import gg.mmorealms.module.mega_evolution.backend.fabric.registry.MegaEvolutionRegistry;
import gg.mmorealms.module.mega_evolution.backend.fabric.world.MegaConfiguredFeatures;
import gg.mmorealms.module.mega_evolution.backend.fabric.world.MegaPlacedFeatures;
import gg.mmorealms.module.mega_evolution.backend.fabric.world.gen.MegaWorldGeneration;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

@Getter
public class MegaEvolutionFabricModule extends MegaEvolutionBackendModule implements ModInitializer {

	@Getter
	@Accessors(fluent = true)
	private static MegaEvolutionFabricModule instance;

	private @Inject MinecraftServer server;
	private @Inject FileManager fileManager;
	private @Inject FabricMiniMessageManager miniMessageManager;

	private MegaEvolutionConfig config;

	public MegaEvolutionFabricModule() {
		MegaEvolutionFabricModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(MegaEvolutionConfig.class));
	}

	@Override
	public void onEnable() {
		CobblemonEventsListener.register();
	}

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets("mmorealms");

		MegaEvolutionRegistry.register();
		MegaConfiguredFeatures.initializeKeys();
		MegaPlacedFeatures.initializeKeys();

		// Generate ores only in wilds
		if (getServerType() == ServerType.WILD || getServerType() == ServerType.WILD_GENERATOR) {
			MegaWorldGeneration.generate();
		}

		this.setup();
	}

}