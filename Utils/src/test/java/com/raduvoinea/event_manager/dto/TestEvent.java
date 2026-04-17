package com.raduvoinea.event_manager.dto;

import com.raduvoinea.event_manager.EventManagerTests;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.event_manager.dto.LocalRequest;
import lombok.Getter;

@Getter
public class TestEvent extends LocalRequest<Integer> {

	private final int number1;
	private final int number2;

	public TestEvent(int number1, int number2) {
		super(0);
		this.number1 = number1;
		this.number2 = number2;
	}

	@Override
	public EventManager getEventManager() {
		return EventManagerTests.EVENT_MANAGER;
	}
}
