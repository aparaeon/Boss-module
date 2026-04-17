package gg.mmorealms.loader.common.dto.event.local;

import com.raduvoinea.utils.event_manager.EventManager;
import gg.mmorealms.loader.common.CommonLoader;

public abstract class LocalEvent extends com.raduvoinea.utils.event_manager.dto.LocalEvent {

	@Override
	public EventManager getEventManager() {
		return CommonLoader.instance().getEventManager();
	}

}