package com.raduvoinea.event_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalEvent {

	private int number1;
	private int number2;

	public ExternalEvent(int number1, int number2) {
		this.number1 = number1;
		this.number2 = number2;
	}

}
