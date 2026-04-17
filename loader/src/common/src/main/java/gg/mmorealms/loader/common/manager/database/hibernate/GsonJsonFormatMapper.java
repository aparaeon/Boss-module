package gg.mmorealms.loader.common.manager.database.hibernate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.manager.database.annotation.InterfaceDeserializationStrategy;
import gg.mmorealms.loader.common.manager.database.enums.InterfaceDeserializationStrategyType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public class GsonJsonFormatMapper implements FormatMapper {

	private static final Gson PLAIN_GSON = new Gson();

	private Gson getGson() {
		return CommonLoader.instance().getGsonSettings().getInternalGsonHolder().value();
	}

	private Gson getDelegateGson() {
		return CommonLoader.instance().getGsonSettings().getNoInterfaceGsonHolder().value();
	}

	private Class<?> resolveArgumentClass(Type type) {
		if (!(type instanceof ParameterizedType parameterizedType)) {
			return null;
		}

		Type[] typeArgs = parameterizedType.getActualTypeArguments();
		if (typeArgs.length != 1) {
			return null;
		}

		if (typeArgs[0] instanceof Class<?> clazz) {
			return clazz;
		}

		if (typeArgs[0] instanceof ParameterizedType parameterizedArg) {
			return (Class<?>) parameterizedArg.getRawType();
		}

		return null;
	}

	private boolean isInterface(Type type) {
		Class<?> argumentClass = resolveArgumentClass(type);
		return argumentClass != null && argumentClass.isInterface();
	}

	@Override
	public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions wrapperOptions) {
		Type type = javaType.getJavaType();

		if (isInterface(type)) {
			Class<?> argumentClass = resolveArgumentClass(type);
			InterfaceDeserializationStrategy strategy = argumentClass != null ?
				argumentClass.getAnnotation(InterfaceDeserializationStrategy.class) :
				null;

			if (strategy != null && strategy.type() == InterfaceDeserializationStrategyType.DISCARD_ON_UNKNOWN) {
				String sanitized = discardUnknownTypes(charSequence.toString(), type);
				return getGson().fromJson(sanitized, type);
			}

			// ERROR_ON_UNKNOWN or no annotation — original behaviour
			return getGson().fromJson(charSequence.toString(), type);
		}

		return getDelegateGson().fromJson(charSequence.toString(), type);
	}

	@Override
	public <T> String toString(T value, JavaType<T> javaType, WrapperOptions wrapperOptions) {
		Type type = javaType.getJavaType();

		if (isInterface(type)) {
			return getGson().toJson(value, type);
		}

		return getDelegateGson().toJson(value, type);
	}

	private String discardUnknownTypes(String raw, Type type) {
		try {
			JsonElement root = PLAIN_GSON.fromJson(raw, JsonElement.class);

			// Not an array — nothing to sanitize, pass through as-is
			if (!root.isJsonArray()) {
				return raw;
			}

			JsonArray array = root.getAsJsonArray();
			Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];

			for (int i = array.size() - 1; i >= 0; i--) {
				try {
					getGson().fromJson(array.get(i), elementType);
				} catch (Exception e) {
					if (hasCause(e, ClassNotFoundException.class)) {
						log.warn("[GsonJsonFormatMapper] Discarding entry — class no longer exists. Raw: {}", array.get(i));
						array.remove(i);
					} else {
						throw e;
					}
				}
			}

			return PLAIN_GSON.toJson(array);
		} catch (Exception e) {
			log.error("[GsonJsonFormatMapper] Failed to pre-scan JSON, falling back to raw.", e);
			return raw;
		}
	}

	@SuppressWarnings("SameParameterValue")
	private boolean hasCause(Throwable t, Class<? extends Throwable> causeType) {
		while (t != null) {
			if (causeType.isInstance(t)) {
				return true;
			}
			t = t.getCause();
		}
		return false;
	}
}