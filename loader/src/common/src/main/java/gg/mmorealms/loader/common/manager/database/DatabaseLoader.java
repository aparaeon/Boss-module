package gg.mmorealms.loader.common.manager.database;

import com.raduvoinea.utils.generic.Time;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.reflections.Reflections;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.dto.database.ISavable;
import gg.mmorealms.loader.common.dto.database.cache.AutoSaveCache;
import gg.mmorealms.loader.common.exception.DatabaseObjectCreationException;
import gg.mmorealms.loader.common.exception.DatabaseSaveException;
import jakarta.persistence.Entity;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public abstract class DatabaseLoader<
		Identifier,
		ObjectInterface extends ISavable,
		LoadedObject extends ObjectInterface
		> {

	protected static final @Getter List<DatabaseLoader<?, ?, ?>> ALL = new ArrayList<>();

	protected final String tableName;
	protected final String identifierName;

	protected final Class<Identifier> identifierClass;
	protected final Class<LoadedObject> loadedObjectClass;

	protected final AutoSaveCache<Identifier, LoadedObject> cache;

	public DatabaseLoader(Class<LoadedObject> loadedObjectClass) {
		this(loadedObjectClass, Time.minutes(5)); // TODO Config
	}

	public DatabaseLoader(Class<LoadedObject> loadedObjectClass, Time autoSaveInterval) {
		this.loadedObjectClass = loadedObjectClass;

		this.tableName = loadedObjectClass.getAnnotation(Entity.class).name();

		Field identifierField = Reflections.getFields(loadedObjectClass).stream()
				.filter(field -> field.isAnnotationPresent(jakarta.persistence.Id.class))
				.findFirst().orElse(null);

		if (identifierField == null) {
			throw new RuntimeException("No identifier field found in " + loadedObjectClass.getName());
		}

		this.identifierName = identifierField.getName();
		//noinspection unchecked
		this.identifierClass = (Class<Identifier>) identifierField.getType();

		this.cache = new AutoSaveCache<>(identifierClass, loadedObjectClass, autoSaveInterval, this::shouldClearCache, this::beforeCacheAutoCommit);
		ALL.add(this);
	}

	protected abstract boolean shouldClearCache(@NotNull Identifier identifier, @NotNull LoadedObject loadedObject);

	protected void beforeCacheAutoCommit() {

	}

	public @Nullable ObjectInterface getByIdentifier(@NotNull Identifier identifier) throws DatabaseObjectCreationException {
		LoadedObject objectFromCache = cache.get(identifier);

		if (objectFromCache != null) {
			return objectFromCache;
		}

		LoadedObject object = loadObject(identifier);

		if (object == null) {
			return null;
		}

		cache(identifier, object);
		return object;
	}

	protected @Nullable Identifier convertIdentifiers(@NotNull String alternativeIdentifierName, @NotNull Object alternativeIdentifier) {
		if(CommonLoader.DUMMY_MODE){
			Logger.warn("Trying to convert identifiers for " + loadedObjectClass.getName() + " but dummy mode is enabled, returning null");
			return null;
		}

		try {
			Field field = Reflections.getField(loadedObjectClass, alternativeIdentifierName);

			if (field == null) {
				Logger.error("Field " + alternativeIdentifierName + " not found in " + loadedObjectClass.getName());
			} else {
				for (Identifier identifier : cache.keySet()) {
					LoadedObject object = cache.get(identifier);
					Object value = field.get(object);

					if (value.equals(alternativeIdentifier)) {
						return identifier;
					}
				}
			}
		} catch (IllegalAccessException error) {
			Logger.error(error);
			Logger.error("Could not access field " + alternativeIdentifierName + " in " + loadedObjectClass.getName());
		}

		Identifier identifier;

		//noinspection JpaQlInspection
		String sql = "SELECT " + identifierName + " FROM " + tableName + " WHERE " + alternativeIdentifierName + " = :alternativeIdentifier";
		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			try {
				//noinspection SqlSourceToSinkFlow
				identifier = session.createQuery(sql, identifierClass)
						.setParameter("alternativeIdentifier", alternativeIdentifier)
						.getSingleResult();

			} catch (org.hibernate.sql.exec.ExecutionException error) {
				alternativeIdentifier = CommonLoader.instance().toJson(alternativeIdentifier);

				//noinspection SqlSourceToSinkFlow
				identifier = session.createQuery(sql, identifierClass)
						.setParameter("alternativeIdentifier", alternativeIdentifier)
						.getSingleResult();
			} catch (NoResultException error) {
				identifier = null;
			}
		}

		return identifier;
	}

	public void clearCache(@NotNull Identifier identifier, boolean save) {
		ScheduleUtils.runTaskAsync(() -> {
			if (save) {
				LoadedObject object = cache.get(identifier);

				if (object == null) {
					return;
				}

				try {
					object.save();
				} catch (DatabaseSaveException exception) {
					Logger.error(exception);
				}
			}

			cache.remove(identifier);
		});
	}

	public void clearAllCache(boolean save) {
		ScheduleUtils.runTaskAsync(() -> {
			for (Map.Entry<Identifier, LoadedObject> entry : cache.entrySet()) {
				if (save) {
					LoadedObject object = entry.getValue();

					if (object == null) {
						continue;
					}

					try {
						object.save();
					} catch (DatabaseSaveException exception) {
						Logger.error(exception);
					}
				}

				cache.remove(entry.getKey());
			}
		});
	}

	protected @Nullable LoadedObject loadObject(@NotNull Identifier identifier) {
		if(CommonLoader.DUMMY_MODE){
			Logger.warn("Trying to load object with identifier " + identifier + " for " + loadedObjectClass.getName() + " but dummy mode is enabled, returning null");
			return null;
		}

		try (Session session = DatabaseManager.instance().getSessionFactory().openSession()) {
			return loadObject(session, identifier);
		} catch (NoResultException exception) {
			return null;
		}
	}

	public void cache(@NotNull Identifier identifier, @NotNull LoadedObject loadedObject) {
		cache.put(identifier, loadedObject);
	}

	public void cache(@NotNull Identifier identifier, @NotNull Object loadedObject) {
		if (!loadedObjectClass.isAssignableFrom(loadedObject.getClass())) {
			Logger.error(new MessageBuilder("Trying to cache object {object} with identifier {identifier} but it is not of type {type}")
					.parse("object", loadedObject)
					.parse("identifier", identifier)
					.parse("type", loadedObjectClass.getName())
			);
		}

		//noinspection unchecked
		cache(identifier, (LoadedObject) loadedObject);
	}

	protected @Nullable LoadedObject loadObject(@NotNull Session session, @NotNull Identifier identifier) {
		return session.get(loadedObjectClass, identifier);
	}

	public @Nullable ObjectInterface getByIndexedFiled(@NotNull String identifierName, @NotNull Object alternativeIdentifier) {
		Identifier identifier = convertIdentifiers(identifierName, alternativeIdentifier);

		if (identifier == null) {
			return null;
		}

		return getByIdentifier(identifier);
	}

}