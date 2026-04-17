package com.raduvoinea.utils.dependency_injection;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.reflections.Reflections;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Getter
public class Injector {

	private final HashMap<Class<?>, Object> dependencies = new HashMap<>();
	private final List<UnresolvedDependency> unresolvedDependencies = new ArrayList<>();
	private final Set<Class<?>> currentlyCreating = new HashSet<>();

	@Getter
	public static class UnresolvedDependency {
		private final Object target;
		private final Field field;
		private final Class<?> dependencyType;

		public UnresolvedDependency(Object target, Field field, Class<?> dependencyType) {
			this.target = target;
			this.field = field;
			this.dependencyType = dependencyType;
		}
	}

	public <Child extends Parent, Parent> Child bind(Child object) {
		dependencies.put(object.getClass(), object);
		return object;
	}

	public <Child extends Parent, Parent> Child bind(Class<Parent> clazz, Child object) {
		dependencies.put(clazz, object);
		return object;
	}

	public void inject(Object object) throws InjectionException {
		inject(object, true);
	}

	public void inject(Object object, boolean allowUnresolved) throws InjectionException {
		for (Field field : Reflections.getFields(object.getClass())) {
			inject(object, field, allowUnresolved);
		}
	}

	private void inject(Object object, Field field, boolean allowUnresolved) throws InjectionException {
		Inject inject = field.getAnnotation(Inject.class);

		if (inject == null) {
			for (Annotation annotation : field.getAnnotations()) {
				if (annotation.getClass().getName().endsWith(".Inject")) {
					Logger.warn("Inject annotation from other libs found. You might want to check if the field is being injected properly. Annotation: " + annotation.getClass().getName());
					return;
				}
			}
			return;
		}

		Object dependency = dependencies.get(field.getType());

		if (dependency == null) {
			if (allowUnresolved) {
				unresolvedDependencies.add(new UnresolvedDependency(object, field, field.getType()));
				Logger.debug("Deferred injection for field: " + field.getName() + " of type: " + field.getType().getName() + " in class: " + object.getClass().getName());
				return;
			}

			throw new InjectionException(
					new MessageBuilder("""
							Failed to inject dependency into field {field} from class {class}
							Missing dependency: {dependency}
							""")
							.parse("class", object.getClass().getName())
							.parse("field", field.getName())
							.parse("dependency", field.getType().getName())
							.parse()
			);
		}

		try {
			Logger.debug("Injecting dependency: " + field.getType().getName() + " into field: " + field.getName() + " from class: " + object.getClass().getName());
			field.setAccessible(true);
			field.set(object, dependencies.get(field.getType()));
		} catch (IllegalAccessException exception) {
			throw new InjectionException(
					new MessageBuilder("Failed to inject dependency {dependency} into the field {field}")
							.parse("field", field.getName())
							.parse("dependency", field.getType().getName())
							.parse(),
					exception
			);
		}
	}

	public int resolveUnresolved() throws InjectionException {
		int resolved = 0;
		Iterator<UnresolvedDependency> iterator = unresolvedDependencies.iterator();

		while (iterator.hasNext()) {
			UnresolvedDependency unresolved = iterator.next();
			Object value = dependencies.get(unresolved.getDependencyType());

			if (value != null) {
				try {
					unresolved.getField().setAccessible(true);
					unresolved.getField().set(unresolved.getTarget(), value);
					Logger.debug("Resolved deferred dependency: " + unresolved.getDependencyType().getName() +
							" into field: " + unresolved.getField().getName() +
							" from class: " + unresolved.getTarget().getClass().getName());
					iterator.remove();
					resolved++;
				} catch (IllegalAccessException exception) {
					throw new InjectionException(
							new MessageBuilder("Failed to resolve deferred dependency {dependency} into field {field}")
									.parse("field", unresolved.getField().getName())
									.parse("dependency", unresolved.getDependencyType().getName())
									.parse(),
							exception
					);
				}
			}
		}

		return resolved;
	}

	public List<UnresolvedDependency> getUnresolvedDependencies() {
		return Collections.unmodifiableList(unresolvedDependencies);
	}

	public void clearUnresolved() {
		unresolvedDependencies.clear();
	}

}
