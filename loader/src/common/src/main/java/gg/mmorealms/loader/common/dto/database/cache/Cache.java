package gg.mmorealms.loader.common.dto.database.cache;

import gg.mmorealms.loader.common.dto.database.ICacheable;
import gg.mmorealms.loader.common.dto.event.CacheEvictEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Cache<Key, CachedObject extends ICacheable> extends ConcurrentHashMap<Key, CachedObject> {

	private final Class<Key> keyClass;
	private final Class<CachedObject> cachedObjectClass;

	public Cache(Class<Key> keyClass, Class<CachedObject> cachedObjectClass) {
		this.keyClass = keyClass;
		this.cachedObjectClass = cachedObjectClass;
	}

	@Override
	public CachedObject put(@NotNull Key key, @NotNull CachedObject object) {
		super.put(key, object);
		return object;
	}

	@Override
	public CachedObject get(@Nullable Object key) {
		if (key == null) {
			return null;
		}

		return super.get(key);
	}

	@Override
	public CachedObject remove(@Nullable Object key) {
		if (key == null) {
			return null;
		}

		CachedObject cachedObject = super.remove(key);

		if (cachedObject == null) {
			return null;
		}

		cachedObject.onEvict();
//		new CacheEvictEvent<>(cachedObjectClass, cachedObject).fireSync();

		return cachedObject;
	}
}
