package gg.mmorealms.loader.common.manager;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.loader.common.dto.LoadedModule;
import gg.mmorealms.loader.common.dto.ModuleID;
import gg.mmorealms.loader.common.exception.*;
import gg.mmorealms.loader.common.utils.ModuleSorter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/*
TODO Make the injecting of dependencies check if the injected dependency is from self module or from a module that the current dependency has a dependency on, if not throw an error
TODO Check if multiple modules have the same id and if so throw an error
 */
@NoArgsConstructor
@Getter
public class ModuleManager {

	private List<LoadedModule> loadedModules = new ArrayList<>();

	private void spacing() {
		for (int i = 0; i < 4; i++) {
			Logger.log("");
		}
	}

	public void registerPreInstantiatedModule(CommonModule moduleInstance) {
		//noinspection unchecked
		this.loadedModules.add(new LoadedModule(
			(Class<CommonModule>) moduleInstance.getClass(),
			moduleInstance
		));
	}

	public void setup() {
		this.spacing();

		Set<Class<CommonModule>> annotatedClasses = CommonLoader.instance().getReflectionsCrawler("gg.mmorealms").getOfType(CommonModule.class, false);
		Logger.good(new MessageBuilder("Found {number} CommonModule classes.")
			.parse("number", annotatedClasses.size())
		);
		this.spacing();

		Logger.info("Creating LoadedModule instances...");
		for (Class<CommonModule> annotatedClass : annotatedClasses) {
			if (getLoadedModule(annotatedClass) != null) {
				continue;
			}

			loadedModules.add(new LoadedModule(annotatedClass));
		}

		int created = getLoadedModulesCountWithState(LoadedModule.State.LOCATED);
		int total = loadedModules.size();

		Logger.goodOrWarn(new MessageBuilder("Created {created}/{total} LoadedModule instances.")
				.parse("created", created)
				.parse("total", total),
			created == total
		);
		this.spacing();


		Logger.info("Ordering modules by dependencies...");
		Logger.info("Removing FAILED modules...");
		loadedModules.removeIf(loadedModule -> loadedModule.getState() == LoadedModule.State.FAILED);
		try {
			loadedModules = ModuleSorter.sortModulesByDependencies(loadedModules);
		} catch (CircularDependencyException exception) {
			throw new FatalLoaderException("Failed to find suitable load order for modules.", exception);
		}
		Logger.good("Successfully found suitable load order for modules.");
		Logger.info("Load order: " +
			String.join(" -> ", loadedModules.stream()
				.map(LoadedModule::getId)
				.map(ModuleID::toString)
				.toList()
			));
		this.spacing();
	}


	public void initAndEnable() {
		createModules();
		initModules();
		enableModules();
	}

	private void createModules() {
		Logger.info("Creating modules...");
		Logger.debug("");

		for (LoadedModule loadedModule : loadedModules) {
			try {
				loadedModule.create();
			} catch (MissingDependencyException | ModuleLoadException exception) {
				Logger.error(exception);
			}
		}

		int createdCount = getLoadedModulesCountWithState(LoadedModule.State.CREATED);
		int totalCount = loadedModules.size();

		Logger.goodOrWarn(new MessageBuilder("Created {created}/{total} modules.\n")
				.parse("created", createdCount)
				.parse("total", totalCount),
			createdCount == totalCount
		);
		this.spacing();
	}

	public void initModules() {
		Logger.info("Initializing modules...");
		Logger.debug("");

		for (LoadedModule loadedModule : loadedModules) {
			try {
				Logger.debug("Initializing module " + loadedModule.getId() + "...");
				loadedModule.init();
			} catch (Exception exception) {
				Logger.error(exception);
			}
		}

		int initializedCount = getLoadedModulesCountWithState(LoadedModule.State.INITIALIZED);
		int totalCount = loadedModules.size();

		Logger.goodOrWarn(new MessageBuilder("Initialized {initialized}/{total} modules.\n")
				.parse("initialized", initializedCount)
				.parse("total", totalCount),
			initializedCount == totalCount
		);
		this.spacing();
	}

	public void enableModules() {
		Logger.info("Enabling modules...");
		Logger.debug("");

		for (LoadedModule loadedModule : loadedModules) {
			try {
				loadedModule.enable();
			} catch (MissingDependencyException | ModuleException exception) {
				Logger.error(exception.getMessage());
			}
		}

		int enabledCount = getLoadedModulesCountWithState(LoadedModule.State.ENABLED);
		int totalCount = loadedModules.size();

		Logger.goodOrWarn(new MessageBuilder("Enabled {initialized}/{total} modules.\n")
				.parse("initialized", enabledCount)
				.parse("total", totalCount),
			enabledCount == totalCount
		);
		this.spacing();
	}

	public LoadedModule getLoadedModule(@NotNull ModuleID id) {
		for (LoadedModule loadedModule : loadedModules) {
			if (id.equals(loadedModule.getId())) {
				return loadedModule;
			}
		}
		return null;
	}

	public LoadedModule getLoadedModule(Class<?> moduleClass) {
		for (LoadedModule loadedModule : loadedModules) {
			if (loadedModule.getModuleClass().equals(moduleClass)) {
				return loadedModule;
			}
		}
		return null;
	}

	private int getLoadedModulesCountWithState(LoadedModule.State state) {
		return loadedModules.stream()
			.filter(loadedModule -> loadedModule.getState() == state)
			.toList().size();
	}

	@Override
	public String toString() {
		MessageBuilder template = new MessageBuilder("Modules ({enabled_count}/{total_count}): {modules}");
		List<LoadedModule> sortedModules = this.loadedModules.stream().sorted(Comparator.comparing(LoadedModule::getId)).toList();

		List<String> modules = sortedModules.stream().map(
			module -> switch (module.getState()) {
				case FAILED -> "<red>";
				case LOCATED, INITIALIZED, CREATED -> "<gold>";
				case ENABLED -> "<green>";
			} + module.getId() + "<white>"
		).toList();

		return template
			.parse("modules", modules)
			.parse("enabled_count", getLoadedModulesCountWithState(LoadedModule.State.ENABLED))
			.parse("total_count", loadedModules.size())
			.parse();
	}

}
