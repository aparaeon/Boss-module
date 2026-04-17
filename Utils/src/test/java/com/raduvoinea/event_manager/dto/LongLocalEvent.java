package com.raduvoinea.event_manager.dto;

import com.raduvoinea.event_manager.EventManagerTests;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.event_manager.dto.LocalEvent;
import com.raduvoinea.utils.event_manager.dto.LocalRequest;

public class LongLocalEvent extends LocalRequest<Boolean> {

	public LongLocalEvent() {
		super(false);
	}

	@Override
	public EventManager getEventManager() {
		return EventManagerTests.EVENT_MANAGER;
	}
}
