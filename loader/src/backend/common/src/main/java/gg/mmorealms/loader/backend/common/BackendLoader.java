package gg.mmorealms.loader.backend.common;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import gg.mmorealms.loader.backend.common.command.MigrateDatabaseCommand;
import gg.mmorealms.loader.backend.common.command.ModulesCommand;
import gg.mmorealms.loader.backend.common.manager.BackendEvents;
import gg.mmorealms.loader.backend.common.manager.Listener;
import gg.mmorealms.loader.backend.common.manager.SyncedDatabaseLoader;
import gg.mmorealms.loader.backend.common.manager.type_adapter.ItemStackTypeAdapter;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.ServerType;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.lucko.spark.common.SparkPlatform;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.slf4j.LoggerFactory;

@Getter
public abstract class BackendLoader extends CommonLoader {

	@Getter
	@Accessors(fluent = true)
	protected static BackendLoader instance;
	protected final ServerType serverType; // exported
	protected MinecraftServer server; // exported
	protected RegistryAccess registryAccess; // exported
	protected BackendEvents backendEvents;

	private @Setter SparkPlatform sparkPlatform;

	public BackendLoader() {
		super(LoggerFactory.getLogger("MMORealms"));

		BackendLoader.instance = this;
		this.serverType = export(ServerType.valueOf(SecretsUtils.getEnvironmentVariable("SERVER_TYPE")));

		this.getEventManager().register(new Listener());

		this.getGsonSettings().updateGson((gsonBuilder) -> {
			new ItemStackTypeAdapter(this.getClassLoader()).register(gsonBuilder);
		});
	}

	@Override
	protected String getJarsRelativePath() {
		return "mods";
	}

	protected void onStart(MinecraftServer server) {
		this.server = export(server, MinecraftServer.class);
		this.registryAccess = export(this.server.registryAccess(), RegistryAccess.class);

		this.backendEvents = new BackendEvents();
		this.backendEvents.registerEvents();

		setupModules();
	}

	protected void onStop(MinecraftServer server) {
		for (DatabaseLoader<?, ?, ?> databaseLoader : SyncedDatabaseLoader.getALL()) {
			databaseLoader.getCache().saveCache(true);
		}
	}

	public abstract BackendMiniMessageManager getMiniMessageManager();

	@Override
	protected void registerLoaderCommands(CommonCommandManager commandManager) {
		commandManager.register(ModulesCommand.class);
		commandManager.register(MigrateDatabaseCommand.class);
	}
}