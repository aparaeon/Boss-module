package com.raduvoinea.utils.reflections;

import com.raduvoinea.utils.logger.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Every method in this class is very computationally expensive. It is recommended to cache the results, and if
 * possible to use the class only in the constructors of modules and managers (one time uses).
 */
@Getter
public class Reflections {

	private final ClassLoader classLoader;
	private final Set<Class<?>> classes = new HashSet<>();
	private final boolean debug;

	public Reflections(@NotNull ClassLoader classLoader) {
		this(classLoader, false);
	}

	public Reflections(@NotNull ClassLoader classLoader, boolean debug) {
		this.classLoader = classLoader;
		String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
		for (String classPathEntry : classPathEntries) {
			if (classPathEntry.endsWith(".jar") || classPathEntry.endsWith(".zip")) {
				registerZip(new File(classPathEntry));
			} else {
				registerDirectory(new File(classPathEntry));
			}
		}

		this.debug = debug;
	}

	public static @NotNull Reflections.Crawler simple(ClassLoader classLoader, Class<?>... classes) {
		return simple(classLoader, Arrays.asList(classes));
	}

	public static @NotNull Reflections.Crawler simple(ClassLoader classLoader, Collection<Class<?>> classes) {
		Reflections reflections = new Reflections(classLoader);
		reflections.registerClasses(classes);
		return reflections.from();
	}

	public Reflections.Crawler from(String... searchDomain) {
		return new Reflections.Crawler(this, searchDomain);
	}

	public Reflections registerZip(File zipFile) {
		if (debug) {
			Logger.debug("Registering zip file: " + zipFile);
		}

		try (ZipFile zip = new ZipFile(zipFile)) {
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if (entry == null || entry.isDirectory()) {
					continue;
				}

				try {
					processFile(entry.getName());
				} catch (Throwable exception) {
					Logger.warn("Reflections failed to find class " + exception.getMessage());
				}
			}
		} catch (Throwable exception) {
			Logger.error(exception);
		}

		return this;
	}

	@SuppressWarnings("unused")
	public Reflections registerDirectories(Collection<File> directories) {
		for (File directory : directories) {
			registerDirectory(directory);
		}

		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public Reflections registerDirectory(File directory) {
		if (!directory.isDirectory()) {
			return this;
		}

		File[] files = directory.listFiles();

		if (files == null) {
			return this;
		}

		Queue<File> toExplore = new LinkedList<>(Arrays.asList(files));

		while (!toExplore.isEmpty()) {
			File file = toExplore.poll();

			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();

				if (subFiles != null) {
					toExplore.addAll(Arrays.asList(subFiles));
				}

				continue;
			}

			if (!file.getName().endsWith(".class")) {
				continue;
			}

			try {
				processFile(file.getAbsolutePath().replace(directory.getAbsolutePath() + File.separator, ""));
			} catch (Throwable exception) {
				Logger.warn(exception);
			}
		}

		return this;
	}

	@SuppressWarnings("unused")
	public Reflections registerClasses(@NotNull Class<?>... classes) {
		return this.registerClasses(Arrays.asList(classes));
	}

	public Reflections registerClasses(@NotNull Collection<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			this.registerClass(clazz);
		}

		return this;
	}

	public Reflections registerClass(@NotNull Class<?> clazz) {
		this.classes.add(clazz);
		return this;
	}

	private void processFile(String fileName) throws ClassNotFoundException {
		if (debug) {
			Logger.debug("Processing file: " + fileName);
		}

		if (!fileName.endsWith(".class")) {
			return;
		}

		fileName = fileName.replace("/", ".");
		fileName = fileName.replace("\\", ".");
		fileName = fileName.replace(".class", "");

		String simpleClassName = fileName.substring(fileName.lastIndexOf('.') + 1);

		// Skip Mixin classes
		if (simpleClassName.contains("Mixin") || simpleClassName.contains("module-info")) {
			return;
		}

		registerClass(classLoader.loadClass(fileName));
	}

	public static @Nullable Field getField(@NotNull Class<?> clazz, String fieldName) {
		Field field = null;

		try {
			field = clazz.getField(fieldName);
		} catch (NoSuchFieldException ignored) {
		}

		if (field == null) {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
			}
		}

		if (field != null) {
			field.setAccessible(true);
		}

		return field;
	}

	public static @NotNull Set<Field> getFields(@NotNull Class<?> clazz) {
		Set<Field> output = new HashSet<>();

		Queue<Class<?>> classesToSearch = new LinkedList<>();
		classesToSearch.add(clazz);

		while (!classesToSearch.isEmpty()) {
			Class<?> searchClass = classesToSearch.poll();

			if (searchClass == null || searchClass == Object.class) {
				continue;
			}

			classesToSearch.add(searchClass.getSuperclass());

			if (searchClass == Enum.class) {
				continue;
			}

			for (Field field : searchClass.getDeclaredFields()) {
				try {
					field.setAccessible(true);
					output.add(field);
				} catch (Throwable exception) {
					Logger.error(exception);
				}
			}
		}

		return output;
	}


	public static @NotNull Set<Method> getMethods(@NotNull Class<?> clazz) {
		Set<Method> output = new HashSet<>();

		Queue<Class<?>> classesToSearch = new LinkedList<>();
		classesToSearch.add(clazz);

		while (!classesToSearch.isEmpty()) {
			Class<?> searchClass = classesToSearch.poll();

			if (searchClass == null) {
				continue;
			}

			Class<?> superclass = searchClass.getSuperclass();

			if (!superclass.equals(Object.class)) {
				classesToSearch.add(searchClass.getSuperclass());
			}

			for (Method method : searchClass.getDeclaredMethods()) {
				method.setAccessible(true);
				output.add(method);
			}
		}

		return output;
	}

	public static Method getCallingMethod(int depth) {
		StackWalker.StackFrame stackFrame = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.walk(stream -> stream
						.skip(depth)
						.findFirst()
						.orElse(null)
				);

		if (stackFrame == null) {
			throw new RuntimeException("StackFrame is null");
		}

		Class<?>[] parameterTypes = stackFrame.getMethodType().parameterArray();
		Class<?> clazz = stackFrame.getDeclaringClass();
		String methodName = stackFrame.getMethodName();

		try {
			return clazz.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException error) {
			Logger.error(error);
		}

		return null;
	}

	@Getter
	public static class Crawler {
		private final Reflections reflections;
		private final Set<String> searchDomain = new HashSet<>();

		public Crawler(Reflections reflections, String... searchDomain) {
			this.reflections = reflections;

			if (searchDomain.length == 0) {
				this.searchDomain.add("");
			}

			for (String domain : searchDomain) {
				if (!domain.endsWith(".") && !domain.isEmpty()) {
					domain += ".";
				}

				this.searchDomain.add(domain);
			}
		}

		public @NotNull Set<Class<?>> getClasses(boolean includeNonConcreteTypes) {
			Set<Class<?>> classes = new HashSet<>();

			for (Class<?> clazz : reflections.getClasses()) {

				if (!includeNonConcreteTypes && (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || Modifier.isAbstract(clazz.getModifiers()))) {
					continue;
				}

				for (String domain : searchDomain) {
					String packageName = clazz.getPackageName();

					if (packageName.startsWith(domain)) {
						classes.add(clazz);
						break;
					}
				}
			}

			return classes;
		}

		public @NotNull Set<Class<?>> getClassesAnnotatedWith(@NotNull Class<? extends Annotation> annotation) {
			return getClassesAnnotatedWith(annotation, true);
		}

		public @NotNull Set<Class<?>> getClassesAnnotatedWith(@NotNull Class<? extends Annotation> annotation, boolean includeNonConcreteTypes) {
			Set<Class<?>> classes = new HashSet<>();

			for (Class<?> clazz : getClasses(includeNonConcreteTypes)) {
				if (clazz.getDeclaredAnnotation(annotation) != null) {
					classes.add(clazz);
				}
			}

			return classes;
		}

		public @NotNull Set<Method> getMethodsAnnotatedWith(@NotNull Class<? extends Annotation> annotation) {
			return getMethodsAnnotatedWith(annotation, true);
		}


		public @NotNull Set<Method> getMethodsAnnotatedWith(@NotNull Class<? extends Annotation> annotation, boolean includeNonConcreteTypes) {
			Set<Method> methods = new HashSet<>();

			for (Class<?> clazz : getClasses(includeNonConcreteTypes)) {
				for (Method method : clazz.getDeclaredMethods()) {
					if (method.getDeclaredAnnotation(annotation) != null) {
						methods.add(method);
					}
				}
			}

			return methods;
		}

		public @NotNull <T> Set<Class<T>> getOfType(@NotNull Class<T> typeClass) {
			return getOfType(typeClass, true);
		}

		public @NotNull <T> Set<Class<T>> getOfType(@NotNull Class<T> typeClass, boolean includeNonConcreteTypes) {
			Set<Class<T>> classes = new HashSet<>();

			for (Class<?> clazz : getClasses(includeNonConcreteTypes)) {
				if (typeClass.isAssignableFrom(clazz)) {
					if (!includeNonConcreteTypes && (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || clazz.isSynthetic())) {
						continue;
					}

					//noinspection unchecked
					classes.add((Class<T>) clazz);
				}
			}

			return classes;
		}

		public @NotNull <T> Set<T> getAndCreateObjectsOfType(@NotNull Class<T> typeClass) {
			Set<T> objects = new HashSet<>();

			for (Class<? extends T> clazz : getOfType(typeClass)) {
				try {
					objects.add(clazz.getConstructor().newInstance());
				} catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
				         InvocationTargetException e) {
					Logger.warn("There was an error while creating object of type " + clazz + ". Please check if you have a no-args constructor.");
				}
			}

			return objects;
		}

	}

}
