package gg.mmorealms.loader.common.dto;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.annotation.Module;
import gg.mmorealms.loader.common.exception.MissingDependencyException;
import gg.mmorealms.loader.common.exception.ModuleException;
import gg.mmorealms.loader.common.exception.ModuleLoadException;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Getter
public class LoadedModule {

	private final Class<CommonModule> moduleClass;
	private Module annotation;
	private @Setter State state;
	private CommonModule moduleInstance;
	private ModuleID id;

	public LoadedModule(Class<CommonModule> moduleClass) {
		this(moduleClass, null);
	}

	public LoadedModule(Class<CommonModule> moduleClass, CommonModule moduleInstance) {
		this.moduleClass = moduleClass;
		this.moduleInstance = moduleInstance;
		this.state = State.FAILED;

		if (!CommonModule.class.isAssignableFrom(moduleClass)) {
			Logger.error(new MessageBuilder("Class {class} is not a subclass of CommonModule.")
				.parse("class", moduleClass.getName())
			);
			return;
		}

		if (Modifier.isAbstract(moduleClass.getModifiers())) {
			Logger.error(new MessageBuilder("Class {class} is abstract.")
				.parse("class", moduleClass.getName())
			);
			return;
		}

		this.annotation = findAnnotation(moduleClass);

		if (this.annotation == null) {
			Logger.error(new MessageBuilder("Class {class} not any of its super classes is not annotated with @Module.")
				.parse("class", moduleClass.getName())
			);
			return;
		}
		this.id = new ModuleID(this.annotation.id());

		this.state = State.LOCATED;
	}

	private Module findAnnotation(Class<?> clazz) {
		Logger.debug("Checking class " + clazz.getName() + " for @Module annotation...");
		if (Object.class.equals(clazz)) {
			return null;
		}

		Module annotation = clazz.getAnnotation(Module.class);

		if (annotation != null) {
			return annotation;
		}

		if (clazz.getSuperclass().equals(clazz)) {
			Logger.error("Class " + clazz.getName() + " has no @Module annotation and does not have any supper classes");
			return null;
		}

		Logger.debug("Class " + clazz.getName() + " does not have @Module annotation, checking superclass...");
		return findAnnotation(clazz.getSuperclass());
	}

	@Override
	public String toString() {
		return new MessageBuilder("{id} v{version} by {authors}")
			.parse("id", annotation.id())
			.parse("version", annotation.version())
			.parse("authors", "[" + String.join(", ", annotation.authors()) + "]")
			.parse();
	}

	public void checkDependencies(State targetState) throws MissingDependencyException {
		for (ModuleID dependencyID : this.getDependencies()) {
			LoadedModule loadedModule = CommonLoader.instance().getModuleManager().getLoadedModule(dependencyID);

			if (loadedModule == null || loadedModule.getState().ordinal() < targetState.ordinal()) {
				this.state = State.FAILED;
				throw new MissingDependencyException(this, dependencyID);
			}
		}
	}

	public void create() throws ModuleLoadException, MissingDependencyException {
		if (this.state != State.LOCATED) {
			Logger.warn(new MessageBuilder("Module {id} is not in the LOCATED state.")
				.parse("id", this.annotation.id())
			);
			return;
		}

		checkDependencies(State.CREATED);

		if (this.moduleInstance != null) {
			Logger.debug(new MessageBuilder("Module {id} already has a moduleInstance. Skipping instantiation...")
				.parse("id", this.getId())
			);
			this.state = State.CREATED;
			return;
		}

		try {
			Constructor<CommonModule> constructor = this.moduleClass.getConstructor();
			this.moduleInstance = constructor.newInstance();
			this.state = State.CREATED;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
		         IllegalAccessException exception) {
			throw new ModuleLoadException(this, "Failed to instantiate module", exception);
		}
	}

	public void init() throws ModuleLoadException, MissingDependencyException, ModuleException {
		if (this.state != State.CREATED) {
			Logger.warn(new MessageBuilder("Module {id} is not in the CREATED state.")
				.parse("id", this.annotation.id())
			);
			return;
		}

		checkDependencies(State.INITIALIZED);

		Logger.log("");
		Logger.log("Initializing module: " + this);

		this.moduleInstance.init();
		this.state = State.INITIALIZED;
	}

	public void enable() throws MissingDependencyException, ModuleException {
		if (this.state != State.INITIALIZED) {
			Logger.warn(new MessageBuilder("Module {id} is not in the INITIALIZED state.")
				.parse("id", this.annotation.id())
			);
			return;
		}
		checkDependencies(State.ENABLED);

		Logger.log("");
		Logger.debug("Enabling module: " + this);

		this.moduleInstance.enable();
		this.state = State.ENABLED;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) {
			return false;
		}

		LoadedModule that = (LoadedModule) object;
		return that.id.equals(this.id);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	public ModuleID[] getDependencies() {
		String processedDepednenciesString = this.annotation.dependencies().replace(" ", "");

		if (processedDepednenciesString.isEmpty()) {
			return new ModuleID[]{};
		}

		return Arrays.stream(processedDepednenciesString.split(",")).map(ModuleID::new).toArray(ModuleID[]::new);
	}

	public enum State {
		FAILED,
		LOCATED,
		CREATED,
		INITIALIZED,
		ENABLED
	}
}
