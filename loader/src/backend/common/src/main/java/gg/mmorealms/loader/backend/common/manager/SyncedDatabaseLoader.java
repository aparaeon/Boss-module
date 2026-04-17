package gg.mmorealms.loader.backend.common.manager;

import com.raduvoinea.utils.generic.Time;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.dto.event.impl.RemoteMethodExecuteRequest;
import gg.mmorealms.loader.common.dto.remote.AutoSaveObject;
import gg.mmorealms.loader.common.exception.DatabaseObjectCreationException;
import gg.mmorealms.loader.common.manager.database.DatabaseLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class SyncedDatabaseLoader<
		Identifier,
		ObjectInterface extends ISavable,
		LoadedObject extends ObjectInterface,
		RemoteObject extends ObjectInterface
		> extends DatabaseLoader<Identifier, ObjectInterface, LoadedObject> {

	protected final static int MAX_ATTEMPTS = 20;
	protected final static int ATTEMPT_TIMEOUT = 200;

	protected final Class<ObjectInterface> objectInterfaceClass;
	protected final Class<RemoteObject> remoteObjectClass;

	public SyncedDatabaseLoader(
			Class<ObjectInterface> objectInterfaceClass,
			Class<LoadedObject> loadedObjectClass,
			Class<RemoteObject> remoteObjectClass
	) {
		this(objectInterfaceClass, loadedObjectClass, remoteObjectClass, Time.minutes(5)); // TODO Config
	}

	public SyncedDatabaseLoader(
			Class<ObjectInterface> objectInterfaceClass,
			Class<LoadedObject> loadedObjectClass,
			Class<RemoteObject> remoteObjectClass,
			Time autoSaveInterval
	) {
		super(loadedObjectClass, autoSaveInterval);

		this.objectInterfaceClass = objectInterfaceClass;
		this.remoteObjectClass = remoteObjectClass;

		//noinspection unchecked
		RemoteMethodExecuteRequest.registerObjectFetch(objectInterfaceClass, (identifier) -> getByIdentifier((Identifier) identifier));
	}

	public @Nullable ObjectInterface getByIdentifier(@NotNull Identifier identifier) throws DatabaseObjectCreationException {
		LoadedObject objectFromCache = cache.get(identifier);

		if (objectFromCache != null) {
			return objectFromCache;
		}

		String remoteServer = getRemoteServer(identifier);

		if (remoteServer == null) {
			return offlineObjectBehavior(identifier);
		}

		ObjectInterface object;

		if (CommonLoader.instance().getRedisConfig().getRedisID().equals(remoteServer)) {
			object = locallyOnlineObjectBehaviour(identifier);
		} else {
			object = remotelyOnlineObjectBehaviour(identifier, remoteServer);
		}

		return processObject(identifier, object);
	}

	private @NotNull ObjectInterface processObject(@NotNull Identifier identifier, @Nullable ObjectInterface object) throws DatabaseObjectCreationException {
		if (object == null) {
			object = createObject(identifier);

			if (object == null) {
				throw new DatabaseObjectCreationException(loadedObjectClass, identifier);
			}
		}

		boolean cache = loadedObjectClass.isAssignableFrom(object.getClass());

		if (cache) {
			//noinspection unchecked
			cache(identifier, (LoadedObject) object);
		}

		return object;
	}

	protected @Nullable ObjectInterface offlineObjectBehavior(@NotNull Identifier identifier) {
		LoadedObject loadedObject = loadObject(identifier);

		if (loadedObject == null) {
			loadedObject = createObject(identifier);
		}

		return AutoSaveObject.create(loadedObject, objectInterfaceClass);
	}

	protected @Nullable ObjectInterface locallyOnlineObjectBehaviour(@NotNull Identifier identifier) {
		return loadObject(identifier);
	}

	protected @Nullable ObjectInterface remotelyOnlineObjectBehaviour(@NotNull Identifier identifier, @NotNull String server) {
		return createRemoteObject(identifier, server);
	}

	public void cache(@NotNull Identifier identifier, @NotNull LoadedObject loadedObject) {
		cache.put(identifier, loadedObject);
	}

	public abstract @Nullable String getRemoteServer(Identifier identifier);

	protected abstract @NotNull RemoteObject createRemoteObject(@NotNull Identifier identifier, @NotNull String server);

	/**
	 * @param identifier The identifier of the object to be created
	 * @return The object created or null if the object could not be created. In case the object could not the created
	 * the upstream will either handle the gg.mmorealms.loader.common.exception or throw a {@link DatabaseObjectCreationException}
	 */
	protected abstract @Nullable LoadedObject createObject(@NotNull Identifier identifier);

}
