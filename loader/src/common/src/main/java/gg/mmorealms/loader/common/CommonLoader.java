package gg.mmorealms.loader.common;

import com.raduvoinea.commandmanager.common.config.CommandManagerConfig;
import com.raduvoinea.commandmanager.common.manager.CommonCommandManager;
import com.raduvoinea.utils.dependency_injection.Injector;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableListGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableMapGsonTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableObjectTypeAdapter;
import com.raduvoinea.utils.file_manager.dto.gson.SerializableSetGsonTypeAdapter;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.generic.time.TimeUnitProviderTypeAdapter;
import com.raduvoinea.utils.generic.utils.NetworkUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.message_builder.MessageBuilderManager;
import com.raduvoinea.utils.message_builder.gson.KvMessageBuilderTypeAdapter;
import com.raduvoinea.utils.message_builder.gson.MessageBuilderListTypeAdapter;
import com.raduvoinea.utils.message_builder.gson.MessageBuilderTypeAdapter;
import com.raduvoinea.utils.redis_manager.dto.RedisConfig;
import com.raduvoinea.utils.redis_manager.dto.gson.RedisEventGsonTypeAdapter;
import com.raduvoinea.utils.redis_manager.dto.gson.RedisRequestGsonTypeAdapter;
import com.raduvoinea.utils.redis_manager.manager.RedisManager;
import com.raduvoinea.utils.reflections.Reflections;
import gg.mmorealms.loader.LoaderBuildConstants;
import gg.mmorealms.loader.common.dto.CommonModule;
import gg.mmorealms.loader.common.dto.Environment;
import gg.mmorealms.loader.common.dto.GsonSettings;
import gg.mmorealms.loader.common.dto.Slf4jLogHandler;
import gg.mmorealms.loader.common.dto.config.DatabaseConfig;
import gg.mmorealms.loader.common.manager.Listener;
import gg.mmorealms.loader.common.manager.ModuleManager;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import gg.mmorealms.loader.common.utils.SecretsUtils;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO When debug ios disabled, do not print the debug to the console but save it to a debug file
 * TODO When the server starts / the loader fails to initialize fully, stop receiving redis events
 */
// TODO Add end to end testing (or at least unit testing)
// TODO Map#getOrDefault(key, null) -> Map#get(key)

@Getter
public abstract class CommonLoader {

	public static final boolean DEBUG_MODE = false;
	public static final String BASE_PERMISSION = "mmorealms.command";

	public static boolean DUMMY_MODE = false;
	public static String PROXY_ID = null;

	@Getter
	@Accessors(fluent = true)
	private static CommonLoader instance;

	private final ModuleManager moduleManager;
	private CommonCommandManager commandManager;

	private final RedisConfig redisConfig;
	private final DatabaseConfig databaseConfig;
	private final CommandManagerConfig commandManagerConfig;

	private final GsonSettings gsonSettings;        // exported
	private final ClassLoader classLoader;          // exported
	private final Reflections reflections;          // exported
	private final FileManager fileManager;          // exported
	private final EventManager eventManager;        // exported
	private final RedisManager redisManager;        // exported
	private final Environment environment;          // exported
	private final Holder<Injector> injectorHolder;  // exported
	private DatabaseManager databaseManager;        // exported

	public static String getProxyID() {
		if (CommonLoader.PROXY_ID == null) {
			CommonLoader.PROXY_ID = SecretsUtils.getEnvironmentVariable("PROXY_ID", "proxy");
		}

		return CommonLoader.PROXY_ID;
	}

	public CommonLoader(@Nullable org.slf4j.Logger slf4jLogger) {
		CommonLoader.instance = this;
		CommonLoader.DUMMY_MODE = SecretsUtils.getEnvironmentVariable("DUMMY_MODE", "false").equalsIgnoreCase("true");

		if (DUMMY_MODE) {
			Logger.warn("DUMMY MODE ENABLED - This instance will not function properly and is only meant for testing and development purposes!");
		}

		Logger.log("/base/log4j2.xml");
		Configurator.reconfigure(
			ConfigurationFactory.getInstance().getConfiguration(
				LoggerContext.getContext(),
				ConfigurationSource.fromUri(URI.create("/base/log4j2.xml"))
			)
		);

		if (slf4jLogger != null) {
			Logger.setInstance(new Slf4jLogHandler(slf4jLogger));
		}

		MessageBuilderManager.init(true);
		MessageBuilderManager.instance().setLegacyMode(true);

		Logger.debug("Initializing " + new MessageBuilder("{id} v{version}")
			.parse("id", LoaderBuildConstants.ID)
			.parse("version", LoaderBuildConstants.VERSION)
			.parse());

		this.injectorHolder = Holder.of(new Injector());
		export(injectorHolder);

		this.environment = export(new Environment(
			Environment.Type.valueOf(SecretsUtils.getEnvironmentVariable("ENVIRONMENT_TYPE")),
			Environment.GameMode.valueOf(SecretsUtils.getEnvironmentVariable("ENVIRONMENT_GAMEMODE"))
		));

		this.classLoader = CommonLoader.class.getClassLoader();
		this.reflections = new Reflections(this.classLoader, false);
		this.gsonSettings = export(new GsonSettings(this.classLoader));

		this.gsonSettings.updateGson((gsonBuilder) -> {
			new MessageBuilderTypeAdapter(this.classLoader).register(gsonBuilder);
			new MessageBuilderListTypeAdapter(this.classLoader).register(gsonBuilder);
			new KvMessageBuilderTypeAdapter(this.classLoader).register(gsonBuilder);

			new SerializableListGsonTypeAdapter(this.classLoader).register(gsonBuilder);
			new SerializableMapGsonTypeAdapter(this.classLoader).register(gsonBuilder);
			new SerializableObjectTypeAdapter(this.classLoader).register(gsonBuilder);
			new SerializableSetGsonTypeAdapter(this.classLoader).register(gsonBuilder);

			new TimeUnitProviderTypeAdapter(this.classLoader).register(gsonBuilder);
		});


		this.fileManager = export(new FileManager(this.gsonSettings.getUserFacingGsonHolder(), "config/core", List.of(
				environment.getGameMode().toString().toLowerCase(),
				""
		)));

		this.commandManagerConfig = fileManager.load(CommandManagerConfig.class);
		this.commandManagerConfig.basePermission = BASE_PERMISSION;

		this.redisConfig = SecretsUtils.loadSecretsConfig(RedisConfig.class, null, !DUMMY_MODE);
		this.databaseConfig = SecretsUtils.loadSecretsConfig(DatabaseConfig.class, null, !DUMMY_MODE);
		this.redisConfig.setRedisID(this.redisConfig.getRedisID().replace("{hostname}", NetworkUtils.getHostname()));

		if (this.getServerID().contains("proxy")) {
			this.redisConfig.setAdditionalListenChannels(
				this.redisConfig.getChannel() + "#proxy"
			);
		}

		this.eventManager = export(new EventManager(this.injectorHolder));
		this.redisManager = export(new RedisManager(this.gsonSettings.getInternalGsonHolder(), this.redisConfig, classLoader, Holder.of(this.eventManager), DEBUG_MODE, DUMMY_MODE));

		gsonSettings.updateGson((gsonBuilder) -> {
			new RedisEventGsonTypeAdapter(classLoader, this.redisManager).register(gsonBuilder);
			new RedisRequestGsonTypeAdapter(classLoader, this.redisManager).register(gsonBuilder);
		});

		this.moduleManager = export(new ModuleManager());
		this.eventManager.register(new Listener());

		//		startMicrometerAndPrometheus();
	}

	protected String legacyClassMapper(String className) {
		String[] split = className.split("\\.");

		if (split.length >= 5) {
			String tld = split[0];
			String domain = split[1];
			String platform = split[2];
			String type = split[3];
			String id = split[4];
			String rest = String.join(".", new ArrayList<>(List.of(split)).subList(5, split.length));

			if (tld.equals("gg") && domain.equals("mmorealms") && (type.equals("module") || type.equals("loader"))) {
				String output = String.join(".", List.of(
					tld, domain, type, id, platform, rest
				));
				Logger.debug(new MessageBuilder("Converting class {old} to {new}")
					.parse("old", className)
					.parse("new", output)
					.parse());
				return output;
			}
		}

		return className;
	}

	protected abstract String getJarsRelativePath();

	protected String getJarName() {
		return "MMORealms-Core";
	}

	protected final void setupModules() {
		this.moduleManager.setup();

		preModuleInit();

		this.commandManager = createCommandManager();
		registerLoaderCommands(this.commandManager);
		this.moduleManager.initAndEnable();
	}

	protected void preModuleInit() {
		this.databaseManager = export(new DatabaseManager(this.databaseConfig));
	}

	protected abstract CommonCommandManager createCommandManager();

	protected abstract void registerLoaderCommands(CommonCommandManager commandManager);

	public <T> @NotNull T fromJson(@NotNull String json, @NotNull Class<T> clazz) {
		return fromJson(json, clazz, false);
	}

	public <T> @NotNull T fromJson(@NotNull String json, @NotNull Class<T> clazz, boolean prettyPrint) {
		return (prettyPrint ? gsonSettings.getUserFacingGsonHolder() : gsonSettings.getInternalGsonHolder()).value().fromJson(json, clazz);
	}

	public @NotNull String toJson(@NotNull Object object) {
		return toJson(object, false);
	}

	public @NotNull String toJson(@NotNull Object object, boolean prettyPrint) {
		return (prettyPrint ? gsonSettings.getUserFacingGsonHolder() : gsonSettings.getInternalGsonHolder()).value().toJson(object);
	}

	public <Parent> Parent export(Parent object) {
		//noinspection unchecked
		return export(object, (Class<Parent>) object.getClass());
	}

	public <Child extends Parent, Parent> Child export(Child object, Class<Parent> clazz) {
		Injector injector = this.injectorHolder.value();

		try {
			injector.inject(object);
		} catch (InjectionException exception) {
			throw exception.toRuntimeException();
		}

		Logger.debug("Exported " + clazz.getName());
		return this.injectorHolder.value().bind(clazz, object);
	}

	public Reflections.Crawler getReflectionsCrawler(String... domain) {
		return this.reflections.from(domain);
	}

	public String getServerID() {
		return this.redisConfig.getRedisID();
	}

	public String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public void preRegisterModule(CommonModule instance) {
		Logger.log("Creating module " + instance.getClass().getName() + " instance...");

		String jarPathString = instance.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().split("%")[0];
		Path jarPath = Path.of(jarPathString);

		Logger.debug("Registering module " + instance.getClass().getName() + " with code source path: " + jarPath);
		Logger.debug(new MessageBuilder("Registering module {module} from jar {jar_path})")
			.parse("module", instance.getClass().getName())
			.parse("jar_path", jarPathString)
			.parse()
		);

		this.reflections.registerZip(jarPath.toFile());
		this.moduleManager.registerPreInstantiatedModule(instance);
	}
}

