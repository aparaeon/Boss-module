package com.raduvoinea.utils.event_manager.dto;

import lombok.Getter;
import lombok.Setter;

public abstract class LocalRequest<Result> implements IEvent<Result> {

	@Getter
	@Setter
	private Result result;

	public LocalRequest(Result defaultResult) {
		this.result = defaultResult;
	}
}
