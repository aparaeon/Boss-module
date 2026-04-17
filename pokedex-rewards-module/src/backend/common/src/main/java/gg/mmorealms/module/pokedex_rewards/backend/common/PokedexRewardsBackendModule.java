package gg.mmorealms.module.pokedex_rewards.backend.common;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.pokedex_rewards.backend.common.config.PokedexRewardsConfig;
import gg.mmorealms.module.pokedex_rewards.common.PokedexRewardsCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class PokedexRewardsBackendModule extends PokedexRewardsCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static PokedexRewardsBackendModule instance;

	private @Inject MinecraftServer server;
	private @Inject DatabaseManager databaseManager;
	private @Inject FileManager fileManager;

	private PokedexRewardsConfig config; // exported

	public PokedexRewardsBackendModule() {
		PokedexRewardsBackendModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(PokedexRewardsConfig.class));
	}

	@Override
	public void onEnable() {

	}

}
