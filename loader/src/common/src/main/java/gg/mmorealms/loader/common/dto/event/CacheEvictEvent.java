package gg.mmorealms.loader.common.dto.event;

import gg.mmorealms.loader.common.dto.event.local.LocalEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CacheEvictEvent<CachedObject> extends LocalEvent {

	private Class<CachedObject> cachedObjectClass;
	private CachedObject cachedObject;

}