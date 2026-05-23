package gg.mmorealms.loader.common.dto.remote;

import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.ISavable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class AutoSaveObject<ObjectInterface extends ISavable> implements InvocationHandler {

	private final ObjectInterface realObject;

	private final List<String> autoSaveMethodPrefixes;
	private final List<Class<? extends Throwable>> ignoredExceptions;

	private AutoSaveObject(@NotNull ObjectInterface realObject) {
		this.realObject = realObject;

		this.autoSaveMethodPrefixes = realObject.autoSaveMethods();
		this.ignoredExceptions = realObject.ignoredExceptions();
	}

	@SuppressWarnings("unchecked")
	public static <ObjectInterface extends ISavable> ObjectInterface create(ObjectInterface object, @NotNull Class<ObjectInterface> clazz) {
		if (object == null) {
			return null;
		}

		return (ObjectInterface) Proxy.newProxyInstance(
			CommonLoader.instance().getClassLoader(),
			new Class[]{clazz},
			new AutoSaveObject<>(object)
		);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			Object output = method.invoke(realObject, args);

			for (String prefix : autoSaveMethodPrefixes) {
				if (method.getName().toLowerCase().contains(prefix)) {
					realObject.save();
				}
			}

			return output;
		} catch (Throwable throwable) {
			Throwable cause = throwable;

			while (cause instanceof InvocationTargetException) {
				cause = throwable.getCause();
			}

			Logger.error(cause);

			if (!ignoredExceptions.contains(cause.getClass())) {
				throw cause;
			}
		}
		return null;
	}
}