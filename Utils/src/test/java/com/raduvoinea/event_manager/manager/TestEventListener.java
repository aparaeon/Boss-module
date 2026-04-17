package com.raduvoinea.event_manager.manager;

import com.raduvoinea.event_manager.dto.*;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;

public class TestEventListener {

	@EventHandler
	public void onTestEvent(TestEvent event) {
		event.setResult(event.getNumber1() + event.getNumber2());
	}

	@EventHandler
	public void onTestLocalEvent(TestLocalEvent event) {
		event.setResult(event.getNumber1() + event.getNumber2());
	}

	@EventHandler
	public void onTestLocalRequest(TestLocalRequest event) {
		event.setResult(event.getNumber1() + event.getNumber2());
	}

	@EventHandler
	public void onExternalEvent(ExternalEvent event) {
		int a = event.getNumber1() + event.getNumber2();// TODO: Implement this method
	}

	@EventHandler
	public void onLongLocalEvent(LongLocalEvent event) throws InterruptedException {
		Thread.sleep(2500);
		event.setResult(true);
	}

}
