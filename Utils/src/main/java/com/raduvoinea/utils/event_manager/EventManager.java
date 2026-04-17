package com.raduvoinea.utils.event_manager;

import com.raduvoinea.utils.dependency_injection.Injector;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.event_manager.dto.EventMethod;
import com.raduvoinea.utils.event_manager.dto.IEvent;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.reflections.Reflections;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class EventManager {

	private final HashMap<Class<?>, List<EventMethod>> methods = new HashMap<>();
	private final HashMap<Class<?>, List<EventMethod>> unsafeMethods = new HashMap<>();
	private final Holder<Injector> injectorHolder;

	private @Nullable ExternalRegistrar externalRegistrar = null;

	@Setter
	private boolean verbose = false;

	public EventManager() {
		this(Holder.empty());
	}

	public EventManager(Holder<Injector> injectorHolder) {
		this.injectorHolder = injectorHolder;
	}

	/**
	 * Used to register an external registrar, that can catch any event that does not implement {@link IEvent}
	 */
	public interface ExternalRegistrar {
		/**
		 * @param object     The parent object where the event method is located
		 * @param method     The actual event method
		 * @param eventClass The event class
		 *
		 * @return true if the method was registered successfully, false otherwise
		 */
		boolean register(Object object, Method method, Class<?> eventClass);

		/**
		 * @param method     The actual event method
		 * @param eventClass The event class
		 *
		 * @return true if the method was unregistered successfully, false otherwise
		 */
		boolean unregister(Method method, Class<?> eventClass);
	}

	/**
	 * Used to register an external registrar, that can catch any event that does not implement {@link IEvent}
	 *
	 * @param externalRegistrar the external registrar
	 */
	public void registerExternalRegistrar(ExternalRegistrar externalRegistrar) {
		Logger.good("[EventManager] Registered external registrar");
		Logger.debug(this);
		this.externalRegistrar = externalRegistrar;
	}

	public @Nullable Object createObject(Class<?> clazz) {
		try {
			Constructor<?> constructor = null;

			if (clazz.getDeclaredConstructors().length != 0) {
				constructor = clazz.getConstructor();
			} else if (clazz.getConstructors().length != 0) {
				constructor = clazz.getConstructor();
			}

			if (constructor == null) {
				Logger.error("No constructors found for class " + clazz.getName());
				return null;
			}

			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
		         NoSuchMethodException error) {
			Logger.error("Failed to register class " + clazz.getName() + ". Was unable to find a no-args constructor");
			Logger.error(error);
		}

		return null;
	}

	public void register(Class<?> clazz) {
		Object object = createObject(clazz);
		if (object == null) {
			return;
		}
		register(object);
	}

	public void register(@NotNull Object object) {
		Logger.debug(new MessageBuilder("Registering object {object}")
			.parse("object", object.getClass().getName())
		);

		if (!injectorHolder.isEmpty()) {
			try {
				injectorHolder.value().inject(object);
				injectorHolder.value().bind(object);
			} catch (InjectionException error) {
				throw new RuntimeException(error);
			}
		}

		for (Method method : Reflections.getMethods(object.getClass())) {
			register(object, method);
		}

		sortMethods();
	}

	public void unregister(@NotNull Class<?> clazz) {
		for (Method method : clazz.getMethods()) {
			unregister(method);
		}
	}

	public void fireUnsafe(@NotNull Object event, boolean suppressExceptions, boolean logIfNoListeners) {
		List<EventMethod> eventMethods = getUnsafeEventMethods(event, logIfNoListeners);

		for (EventMethod method : eventMethods) {
			method.fire(event, suppressExceptions, false);
		}
	}

	public <T> T fireSync(@NotNull IEvent<T> event, boolean suppressExceptions) {
		return this.fireSync(event, suppressExceptions, false);
	}

	public <T> T fireSync(@NotNull IEvent<T> event, boolean suppressExceptions, boolean isOffThread) {
		List<EventMethod> eventMethods = getEventMethods(event, true);

		for (EventMethod method : eventMethods) {
			method.fire(event, suppressExceptions, isOffThread);
		}

		return event.getResult();
	}

	public <T> CompletableFuture<T> fireAsync(@NotNull IEvent<T> event, boolean suppressExceptions, long timeoutMilliseconds) {
		return ScheduleUtils
			.runTaskAsync(() -> this.fireSync(event, suppressExceptions, true))
			.orTimeout(timeoutMilliseconds, TimeUnit.MILLISECONDS);
	}

	private @NotNull <T> List<EventMethod> getEventMethods(@NotNull IEvent<T> event, boolean logIfNoListeners) {
		return getEventMethodsGeneric(event, methods, logIfNoListeners);
	}

	private @NotNull List<EventMethod> getUnsafeEventMethods(@NotNull Object event, boolean logIfNoListeners) {
		return getEventMethodsGeneric(event, unsafeMethods, logIfNoListeners);
	}

	private @NotNull List<EventMethod> getEventMethodsGeneric(@NotNull Object event, Map<Class<?>, List<EventMethod>> map, boolean logIfNoListeners) {
		Class<?> eventClass = event.getClass();
		List<EventMethod> eventMethods = map.get(eventClass);

		if (eventMethods == null || eventMethods.isEmpty()) {
			if (logIfNoListeners) {
				Logger.warn(
					new MessageBuilder("No listeners found for event {event}")
						.parse("event", eventClass.getSimpleName())
				);
			}

			return new ArrayList<>();
		}

		return eventMethods;
	}

	protected Class<?> getEventClass(@NotNull Method method) {
		if (!method.isAnnotationPresent(EventHandler.class)) {
			if (verbose) {
				Logger.debug(new MessageBuilder("Method {class}#{method} does not have the EventHandler annotation")
					.parse("class", method.getDeclaringClass())
					.parse("method", method.getName())
				);
			}
			return null;
		}

		EventHandler annotation = method.getAnnotation(EventHandler.class);

		if (annotation.ignore()) {
			Logger.debug(new MessageBuilder("Ignoring method {class}#{method}")
				.parse("class", method.getDeclaringClass())
				.parse("method", method.getName())
			);
			return null;
		}

		if (method.getParameterCount() != 1) {
			Logger.error(new MessageBuilder("Method {method} from class {class} has {count} parameters, expected 1")
				.parse("method", method.getName())
				.parse("class", method.getDeclaringClass())
				.parse("count", method.getParameterCount())
			);
			return null;
		}

		return method.getParameterTypes()[0];
	}

	private void register(Object parentObject, Method method) {
		Class<?> eventClass = getEventClass(method);
		if (eventClass == null) {
			return;
		}

		if (IEvent.class.isAssignableFrom(eventClass)) {
			registerGeneric(parentObject, method, eventClass, methods);
			return;
		}

		if (this.externalRegistrar == null) {
			registerGeneric(parentObject, method, eventClass, unsafeMethods);
			return;
		}

		boolean result = externalRegistrar.register(parentObject, method, eventClass);
		if (!result) {
			Logger.error(new MessageBuilder("Failed to register method {class}#{method} ({eventClass})")
				.parse("method", method.getName())
				.parse("class", method.getDeclaringClass())
				.parse("eventClass", eventClass.getName())
			);
			Logger.debug(this);
		}

	}

	private void unregister(Method method) {
		Class<?> eventClass = getEventClass(method);
		if (eventClass == null) {
			return;
		}

		if (!IEvent.class.isAssignableFrom(eventClass)) {
			if (externalRegistrar == null) {
				unregisterGeneric(method, eventClass, unsafeMethods);
				return;
			}

			boolean result = externalRegistrar.unregister(method, eventClass);
			if (!result) {
				Logger.error(new MessageBuilder("Failed to unregister method {class}#{method} ({eventClass})")
					.parse("method", method.getName())
					.parse("class", method.getDeclaringClass())
					.parse("eventClass", eventClass.getName())
				);
			}
			return;
		}

		unregisterGeneric(method, eventClass, methods);
	}

	private void registerGeneric(Object parentObject, Method method, Class<?> eventClass, HashMap<Class<?>, List<EventMethod>> map) {
		Logger.debug(new MessageBuilder("Registering method {class}#{method}...")
			.parse("method", method.getName())
			.parse("class", method.getDeclaringClass())
		);

		List<EventMethod> eventMethods = map.getOrDefault(eventClass, new ArrayList<>());
		eventMethods.add(new EventMethod(parentObject, method));
		map.put(eventClass, eventMethods);
	}

	private void unregisterGeneric(Method method, Class<?> eventClass, HashMap<Class<?>, List<EventMethod>> map) {
		Logger.warn(new MessageBuilder("Unregistering unsafe method {class}#{method}...")
			.parse("method", method.getName())
			.parse("class", method.getDeclaringClass())
		);

		List<EventMethod> eventMethods = map.getOrDefault(eventClass, new ArrayList<>());
		boolean result = eventMethods.removeIf(eventMethod -> eventMethod.getMethod().equals(method));

		if (!result) {
			Logger.error(new MessageBuilder("Failed to unregister method {class}#{method} ({eventClass})")
				.parse("method", method.getName())
				.parse("class", method.getDeclaringClass())
				.parse("eventClass", eventClass.getName())
			);
			return;
		}

		map.put(eventClass, eventMethods);
	}

	private void sortMethods() {
		for (Class<?> eventClass : methods.keySet()) {
			List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
			eventMethods.sort(EventMethod.Comparator.getInstance());
			methods.put(eventClass, eventMethods);
		}

		for (Class<?> eventClass : unsafeMethods.keySet()) {
			List<EventMethod> eventMethods = unsafeMethods.getOrDefault(eventClass, new ArrayList<>());
			eventMethods.sort(EventMethod.Comparator.getInstance());
			unsafeMethods.put(eventClass, eventMethods);
		}
	}


}
