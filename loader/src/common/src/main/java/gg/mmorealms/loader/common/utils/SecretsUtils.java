package gg.mmorealms.loader.common.utils;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.reflections.Reflections;
import gg.mmorealms.loader.common.CommonLoader;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SecretsUtils {

	public static <T> List<T> loadSecretsConfigs(Class<T> clazz, int count) {
		List<T> result = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			String prefix = clazz.getSimpleName() + "_" + i;

			try {
				result.add(loadSecretsConfig(clazz, prefix, true));
			} catch (RuntimeException exception) {
				return List.of(loadSecretsConfig(clazz));
			}
		}

		return result;
	}

	public static <T> T loadSecretsConfig(Class<T> clazz) {
		return loadSecretsConfig(clazz, null);
	}

	public static <T> T loadSecretsConfig(Class<T> clazz, @Nullable String prefix) {
		return loadSecretsConfig(clazz, prefix, true);
	}

	public static <T> T loadSecretsConfig(Class<T> clazz, @Nullable String prefix, boolean throwOnFailure) {
		if (prefix == null) {
			prefix = clazz.getSimpleName();
		}

		T instance;

		try {
			instance = clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
		         NoSuchMethodException exception) {
			Logger.error(new MessageBuilder("Failed to construct secret instance of {type} Reverting back to file based configuration.")
				.parse("type", clazz.getName())
				.parse()
			);
			Logger.error(exception);
			return CommonLoader.instance().getFileManager().load(clazz);
		}

		for (Field field : Reflections.getFields(clazz)) {
			String envName = toScreamingSnakeCase(prefix + "_" + field.getName());
			String value = System.getenv(envName);

			if (value == null) {
				String message = new MessageBuilder("Failed to load secret {env_name} from environment variables.")
					.parse("env_name", envName)
					.parse();

				if (throwOnFailure) {
					throw new RuntimeException(message);
				}

				Logger.error(message);
				return CommonLoader.instance().getFileManager().load(clazz);
			}

			try {
				trySet(instance, field, value);
				if (CommonLoader.DEBUG_MODE) {
					Logger.debug(new MessageBuilder("Set secret {env_name} to {value}")
						.parse("class", clazz.getSimpleName())
						.parse("env_name", envName)
						.parse("value", value)
						.parse()
					);
				}
			} catch (IllegalAccessException exception) {
				String message = new MessageBuilder("Failed to load secret {env_name} from environment variables.")
					.parse("env_name", envName)
					.parse();

				if (throwOnFailure) {
					throw new RuntimeException(message);
				}

				Logger.error(message);
				Logger.error(exception);

				return CommonLoader.instance().getFileManager().load(clazz);
			}
		}

		return instance;
	}

	private static void trySet(Object instance, Field field, String value) throws IllegalAccessException {
		Class<?> type = field.getType();

		if (type.equals(String.class)) {
			field.set(instance, value);
			return;
		}
		if (type.equals(int.class) || type.equals(Integer.class)) {
			field.set(instance, Integer.parseInt(value));
			return;
		}
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			field.set(instance, Boolean.parseBoolean(value));
			return;
		}
		if (type.equals(long.class) || type.equals(Long.class)) {
			field.set(instance, Long.parseLong(value));
			return;
		}
		if (type.equals(double.class) || type.equals(Double.class)) {
			field.set(instance, Double.parseDouble(value));
			return;
		}
		if (type.equals(float.class) || type.equals(Float.class)) {
			field.set(instance, Float.parseFloat(value));
		}
	}

	public static String toScreamingSnakeCase(String input) {
		return input.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
	}

	public static @Nullable String getEnvironmentVariable(String key) {
		return getEnvironmentVariable(key, null);
	}

	public static String getEnvironmentVariable(String key, String defaultValue) {
		String envValue = System.getenv(key);

		if (envValue != null) {
			return envValue;
		}

		Logger.warn(new MessageBuilder("Environment variable {key} not found. Using default value of `{default}`.")
			.parse("key", key)
			.parse("default", defaultValue)
			.parse()
		);
		return defaultValue;
	}

}
