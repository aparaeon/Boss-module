package gg.mmorealms.loader.common.manager;

import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.logger.Logger;
import gg.mmorealms.loader.common.dto.event.impl.RemoteExecuteEvent;
import gg.mmorealms.loader.common.dto.event.impl.RemoteMethodExecuteRequest;

public class Listener {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@EventHandler
	private void onRemoteMethodExecute(RemoteMethodExecuteRequest event) {
		try {
			event.setResult(event.invoke());
		} catch (Throwable error) {
			Logger.error(error);
			Logger.error("Error while executing remote method: " + event.toString());
		}
	}

	@EventHandler
	public void onRemoteExecuteEvent(RemoteExecuteEvent event) {
		event.fireAll();
	}


}