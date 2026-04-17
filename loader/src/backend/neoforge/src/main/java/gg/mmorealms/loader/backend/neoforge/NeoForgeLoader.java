package gg.mmorealms.loader.backend.neoforge;

import com.raduvoinea.commandmanager.backend.common.manager.BackendMiniMessageManager;
import com.raduvoinea.commandmanager.backend.neoforge.NeoForgeCommandManager;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.common.manager.CommonMiniMessageManager;
import gg.mmorealms.loader.LoaderBuildConstants;
import gg.mmorealms.loader.backend.common.BackendLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(LoaderBuildConstants.ID)
public class NeoForgeLoader extends BackendLoader {

	private BackendMiniMessageManager miniMessageManager;

	public NeoForgeLoader() {
		super();

		NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> onStart(event.getServer()));
		NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> onStop(event.getServer()));
	}

	@Override
	public BackendMiniMessageManager getMiniMessageManager() {
		return this.miniMessageManager;
	}

	@Override
	protected CommonCommandManager createCommandManager() {
		NeoForgeCommandManager commandManager = new NeoForgeCommandManager(
				this.getReflectionsCrawler().getReflections().from("gg.mmorealms"),
				this.getCommandManagerConfig(),
				this.server,
				this.getInjectorHolder()
		);
		this.miniMessageManager = commandManager.getMiniMessageManager();

		export(commandManager, CommonCommandManager.class);
		export(commandManager, NeoForgeCommandManager.class);
		export(this.miniMessageManager, BackendMiniMessageManager.class);
		export(this.miniMessageManager, CommonMiniMessageManager.class);

		return commandManager;
	}

}
