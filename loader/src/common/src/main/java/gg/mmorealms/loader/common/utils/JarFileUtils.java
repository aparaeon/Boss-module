package gg.mmorealms.loader.common.utils;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarFileUtils {

	public static @Nullable String readFileFromJar(File jar, String filePath) throws IOException, IllegalStateException {
		try (JarFile jarFile = new JarFile(jar)) {
			ZipEntry entry = jarFile.getEntry(filePath);

			if (entry == null) {
				return null;
			}

			InputStream inputStream = jarFile.getInputStream(entry);

			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	public static List<Class<?>> getClassesInJar(File jarFile, ClassLoader classLoader) throws IOException {
		List<Class<?>> classes = new ArrayList<>();

		for (String className : getClassesInJar(jarFile)) {
			try {
				Class<?> clazz = classLoader.loadClass(className);
				classes.add(clazz);
			} catch (Throwable exception) {
				// TODO Maybe do something about this
				Logger.warn(new MessageBuilder("Failed to load class {class_name}")
						.parse("class_name", className)
						.parse()
				);
				Logger.warn(exception);
			}
		}

		return classes;
	}

	public static List<String> getClassesInJar(File jarFile) throws IOException {
		List<String> classes = new ArrayList<>();

		try (JarFile jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					String className = name.substring(0, name.length() - 6);
					className = className.replace('/', '.');
					classes.add(className);
				}
			}
		}

		return classes;
	}

}
