package com.raduvoinea.redis_manager.manager;

import com.raduvoinea.redis_manager.dto.event_serialization.ComplexEvent1;
import com.raduvoinea.redis_manager.dto.event_serialization.SimpleEvent1;
import com.raduvoinea.redis_manager.dto.event_serialization.SimpleEvent2;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class TestListener {

	@EventHandler
	public void onSimpleEvent1(SimpleEvent1 event) {
		event.setResult(event.getA() + event.getB());
	}

	@EventHandler
	public void onSimpleEvent2(SimpleEvent2 event) {
		StringBuilder output = new StringBuilder();

		for (String s : event.getA()) {
			output.append(s).append(event.getB());
		}

		event.setResult(output.toString());
	}

	@EventHandler
	public void onComplexEvent1(ComplexEvent1 event) {
		List<String> output = new ArrayList<>(event.getA());
		output.add(event.getB());

		event.setResult(output);
	}

}
