package gg.mmorealms.loader.common.manager;

import com.raduvoinea.utils.reflections.Reflections;
import gg.mmorealms.loader.common.exception.ModuleLoadException;
import lombok.Getter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

@Getter
public class ModuleClassLoader extends URLClassLoader {
	private final Reflections reflections;

	public ModuleClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
		reflections = new Reflections(this, true); // TODO change to variable
	}

	public void registerModule(URL jarURL) {
		addURL(jarURL);
		reflections.registerZip(new File(jarURL.getFile()));
	}

	public void registerModule(URI jarURI) throws ModuleLoadException {
		try {
			registerModule(jarURI.toURL());
		} catch (MalformedURLException exception) {
			throw new ModuleLoadException(jarURI, "Failed to load module from URI", exception);
		}
	}

}
