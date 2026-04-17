package com.raduvoinea.utils.event_manager.dto;

public abstract class LocalEvent implements IEvent<Void> {

	@Override
	public Void getResult() {
		return null;
	}
}
