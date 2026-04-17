package gg.mmorealms.loader.backend.fabric;

import com.raduvoinea.commandmanager.backend.common.manager.BackendCommandManager;
import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.commandmanager.backend.fabric.FabricCommandManager;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.common.manager.CommonMiniMessageManager;
import gg.mmorealms.loader.backend.common.BackendLoader;
import gg.mmorealms.loader.backend.fabric.manager.FabricBackendEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class FabricLoader extends BackendLoader implements ModInitializer {

	private BackendMiniMessageManager miniMessageManager;

	@Override
	protected FabricCommandManager createCommandManager() {
		FabricCommandManager commandManager = new FabricCommandManager(
				this.getReflectionsCrawler().getReflections().from("gg.mmorealms"),
				this.getCommandManagerConfig(),
				this.server,
				this.getInjectorHolder()
		); // TODO document the gg.mmorealms
		export(commandManager, CommonCommandManager.class);
		export(commandManager, FabricCommandManager.class);
		export(commandManager, BackendCommandManager.class);

		this.miniMessageManager = commandManager.getMiniMessageManager();
		export(this.miniMessageManager);
		export(this.miniMessageManager, CommonMiniMessageManager.class);
		export(this.miniMessageManager, BackendMiniMessageManager.class);

		return commandManager;
	}

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onStop);
	}

	@Override
	public BackendMiniMessageManager getMiniMessageManager() {
		return this.miniMessageManager;
	}

	@Override
	protected void onStart(MinecraftServer server) {
		new FabricBackendEvents().registerEvents();
		super.onStart(server);
	}
}
