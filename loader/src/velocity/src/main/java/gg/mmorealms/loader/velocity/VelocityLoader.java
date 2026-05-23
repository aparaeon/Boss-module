package gg.mmorealms.loader.velocity;

import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.commandmanager.common.manager.CommonMiniMessageManager;
import com.raduvoinea.commandmanager.velocity.manager.VelocityCommandManager;
import com.raduvoinea.commandmanager.velocity.manager.VelocityMiniMessageManager;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.logger.Logger;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.mmorealms.loader.LoaderBuildConstants;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.velocity.command.ProxyModulesCommand;
import gg.mmorealms.loader.velocity.manager.Listener;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Plugin(
	id = LoaderBuildConstants.ID,
	name = LoaderBuildConstants.ID,
	version = LoaderBuildConstants.VERSION,
	authors = {"Radu Voinea"},
	dependencies = {
		@Dependency(id = "luckperms"),
		@Dependency(id = "packetevents"),
	}
)
@Getter
public class VelocityLoader extends CommonLoader {

	@Getter
	@Accessors(fluent = true)
	private static VelocityLoader instance;

	private final ProxyServer proxy; // exported
	private VelocityMiniMessageManager miniMessageManager; // exported

	@com.google.inject.Inject
	public VelocityLoader(ProxyServer proxy) {
		super(LoggerFactory.getLogger("MMORealms"));
		VelocityLoader.instance = this;

		this.proxy = export(proxy, ProxyServer.class);
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		registerVelocityEvents();

		this.getEventManager().register(new Listener());

		setupModules();
	}

	private void registerVelocityEvents() {
		this.getEventManager().registerExternalRegistrar(new EventManager.ExternalRegistrar() {
			@Override
			public boolean register(Object object, Method method, Class<?> eventClass) {
				proxy.getEventManager().register(VelocityLoader.instance(), eventClass, (event) -> {
					try {
						method.setAccessible(true);
						method.invoke(object, event);
					} catch (IllegalAccessException | InvocationTargetException error) {
						Logger.error(error);
					}
				});

				return true;
			}

			@Override
			public boolean unregister(Method method, Class<?> eventClass) {
				Logger.warn("Unregistering velocity listeners is not yet supported");
				return false;
			}
		});
	}

	@Override
	protected String getJarsRelativePath() {
		return "plugins";
	}

	@Override
	protected VelocityCommandManager createCommandManager() {
		VelocityCommandManager commandManager = new VelocityCommandManager(this, this.proxy,
			this.getReflectionsCrawler("gg.mmorealms"),
			this.getCommandManagerConfig(), this.getInjectorHolder());
		miniMessageManager = export(commandManager.getMiniMessageManager(), VelocityMiniMessageManager.class);
		export(commandManager.getMiniMessageManager(), CommonMiniMessageManager.class);
		export(commandManager.getMiniMessageManager(), VelocityMiniMessageManager.class);
		export(commandManager, VelocityCommandManager.class);
		export(commandManager, CommonCommandManager.class);
		return commandManager;
	}

	@Override
	protected void registerLoaderCommands(CommonCommandManager commandManager) {
		commandManager.register(ProxyModulesCommand.class);
	}

}