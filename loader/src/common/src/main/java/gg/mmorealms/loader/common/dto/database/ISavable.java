package gg.mmorealms.loader.common.dto.database;

import gg.mmorealms.loader.common.dto.remote.AutoSaveObject;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;

import java.util.List;

public interface ISavable extends ICacheable {

	void save() throws DatabaseSaveException;

	/**
	 * {@link AutoSaveObject} uses this method to determine after which
	 * methods to call the {@link #save()} method.
	 *
	 * @return List of method names to call {@link #save()} after.
	 */
	default List<String> autoSaveMethods() {
		return List.of(
				"set",
				"add",
				"remove"
		);
	}

	/**
	 * {@link AutoSaveObject} uses this method to determine which
	 * exceptions to ignore and not throw down the call stack during the {@link #save()} method.
	 *
	 * @return List of exceptions to ignore.
	 */
	default List<Class<? extends Throwable>> ignoredExceptions() {
		return List.of();
	}

}
