package gg.mmorealms.loader.common.dto;

import com.raduvoinea.commandmanager.common.command.CommonCommand;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.reflections.Reflections;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.common.exception.ModuleLoadException;
import gg.mmorealms.loader.common.utils.MojangUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO move the initialization logic to LoadedModule
public interface CommonModule {

	default Reflections.Crawler getReflectionsCrawler() {
		return CommonLoader.instance().getReflectionsCrawler(getDomain());
	}

	private String getDomain() {
		String searchDomain = this.getClass().getPackageName();
		String[] searchDomainSplit = searchDomain.split("\\.");

		if (searchDomain.length() < 5) {
			Logger.error("Invalid package name: " + searchDomain);
			throw new RuntimeException(new ModuleLoadException(this, "Invalid package name: " + searchDomain));
		}

		return searchDomainSplit[0] + "." + searchDomainSplit[1] + "." + searchDomainSplit[2] + "." + searchDomainSplit[3] + "." + searchDomainSplit[4];
	}

	void onInit() throws ModuleException;

	void onEnable() throws ModuleException;

	default <Parent> Parent export(Parent object) {
		return CommonLoader.instance().export(object);
	}

	default <Child extends Parent, Parent> Child export(Child object, Class<Parent> clazz) {
		return CommonLoader.instance().export(object, clazz);
	}

	default String getServerID() {
		return CommonLoader.instance().getRedisConfig().getRedisID();
	}

	default String getPrettyName() {
		return getServerID().split("\\.")[0];
	}

	default void init() throws ModuleLoadException, ModuleException {
		injectDependencies();
		this.onInit();
	}

	default void enable() throws ModuleException {
		registerListeners();
		registerCommands();

		this.onEnable();
	}

	private void registerListeners() {
		Set<Class<?>> classes = this.getReflectionsCrawler().getMethodsAnnotatedWith(EventHandler.class)
			.stream().map(Method::getDeclaringClass)
			.collect(Collectors.toSet());

		for (Class<?> clazz : classes) {
			Logger.debug("Registering event listener: " + clazz.getName());
			registerListener(clazz);
		}
	}

	default void registerListener(Class<?> clazz) {
		Object object = CommonLoader.instance().getEventManager().createObject(clazz);

		if (object == null) {
			Logger.error("Failed to create object for class: " + clazz.getName());
			return;
		}

		CommonLoader.instance().getEventManager().register(object);
	}

	private void registerCommands() {
		this.getReflectionsCrawler().getOfType(CommonCommand.class).forEach(commandClass -> {
			try {
				CommonLoader.instance().getCommandManager().register(commandClass);
			} catch (Throwable error) {
				Logger.error(error);
				Logger.error("Failed to enable  " + commandClass.getName() + " from module " + this);
			}
		});
	}

	default void injectDependencies() throws ModuleLoadException {
		try {
			CommonLoader.instance().getInjectorHolder().value().inject(this);
		} catch (InjectionException exception) {
			throw new ModuleLoadException(this, "Failed to inject dependencies", exception);
		}
	}

	default <T> @NotNull T fromJson(@NotNull String json, @NotNull Class<T> clazz) {
		return CommonLoader.instance().fromJson(json, clazz);
	}

	default @NotNull String toJson(@NotNull Object object) {
		return CommonLoader.instance().toJson(object);
	}

	void sendMessage(Object target, String message);

	/**
	 * Resolves a username or uuid-string to a {@link UUID}, hitting Mojang's API as a fallback when only
	 * a username was provided.
	 *
	 * @param uuidOrUsername username or uuid-string
	 * @return resolved UUID, or {@code null} if the username could not be resolved
	 */
	default @Nullable UUID getUUID(@NotNull String uuidOrUsername) {
		try {
			return UUID.fromString(uuidOrUsername);
		} catch (IllegalArgumentException exception) {
			return MojangUtils.getUUID(uuidOrUsername);
		}
	}

	default Module getModuleAnnotation() {
		return CommonLoader.instance().getModuleManager().getLoadedModule(this.getClass()).getAnnotation();
	}

	enum State {
		LOADED,
		INITIALIZED,
		ENABLED
	}
}
