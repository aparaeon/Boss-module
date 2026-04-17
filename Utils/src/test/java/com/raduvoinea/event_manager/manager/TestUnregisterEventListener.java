package com.raduvoinea.event_manager.manager;

import com.raduvoinea.event_manager.dto.TestUnregisterEvent;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;

public class TestUnregisterEventListener {

	@EventHandler
	public void onTestUnregisterEvent(TestUnregisterEvent event) {
		event.setResult(event.getNumber1() + event.getNumber2());
	}

}
