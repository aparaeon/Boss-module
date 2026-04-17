package gg.mmorealms.loader.common.dto.event.local;

import com.raduvoinea.utils.event_manager.EventManager;
import gg.mmorealms.loader.common.CommonLoader;

public abstract class LocalRequest<Result> extends com.raduvoinea.utils.event_manager.dto.LocalRequest<Result> {

	public LocalRequest(Result defaultResult) {
		super(defaultResult);
	}

	@Override
	public EventManager getEventManager() {
		return CommonLoader.instance().getEventManager();
	}
}
