package gg.mmorealms.module.pokemon.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.backend.common.dto.BackendModule;
import gg.mmorealms.loader.backend.common.manager.BackendPlayerDependentDatabaseLoader;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.module.pokemon.backend.common.dto.database.PokemonData;
import gg.mmorealms.module.pokemon.backend.common.manager.PokemonPlatformImplementation;
import gg.mmorealms.module.pokemon.common.PokemonCommonModule;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

@Getter
public abstract class PokemonBackendModule extends PokemonCommonModule implements BackendModule {

	@Getter
	@Accessors(fluent = true)
	private static PokemonBackendModule instance;

	private @Inject MinecraftServer server;
	private @Inject DatabaseManager databaseManager;
	private @Inject RegistryAccess registryAccess;
	private @Inject FileManager fileManager;
	private @Inject BackendMiniMessageManager miniMessageManager;

	private PokemonPlatformImplementation platformImplementation;

	private PokemonConfig config; // exported

	public PokemonBackendModule() {
		PokemonBackendModule.instance = this;
	}

	@Override
	public void onInit() {
		this.config = export(fileManager.load(PokemonConfig.class));

		this.platformImplementation = this.createPlatformImplementation();
		export(this.platformImplementation);
		export(this.platformImplementation, PokemonPlatformImplementation.class);

		BackendPlayerDependentDatabaseLoader.registerPlayerMethod(
				"Cobblemon Data",
				(player) -> {

				},
				(player) -> {
					try {
						new PokemonData(player).save();
					} catch (DatabaseSaveException e) {
						Logger.error(e);
					}
				},
				Time.minutes(5)
		);
	}

	@Override
	public void onEnable() {
	}

	protected abstract PokemonPlatformImplementation createPlatformImplementation();
}