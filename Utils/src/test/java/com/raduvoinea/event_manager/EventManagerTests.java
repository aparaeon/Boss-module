package com.raduvoinea.event_manager;

import com.raduvoinea.event_manager.dto.*;
import com.raduvoinea.event_manager.manager.TestEventListener;
import com.raduvoinea.event_manager.manager.TestUnregisterEventListener;
import com.raduvoinea.utils.event_manager.EventManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EventManagerTests {

	public static final EventManager EVENT_MANAGER = new EventManager();
	private static final List<Method> EXTERNAL_METHODS = new ArrayList<>();

	@BeforeAll
	public static void setup() {
		EVENT_MANAGER.registerExternalRegistrar(new EventManager.ExternalRegistrar() {
			@Override
			public boolean register(Object object, Method method, Class<?> eventClass) {
				EXTERNAL_METHODS.add(method);
				return true;
			}

			@Override
			public boolean unregister(Method method, Class<?> eventClass) {
				// Not supported
				return false;
			}
		});
		EVENT_MANAGER.register(new TestEventListener());
	}

	@Test
	public void testEvent() {
		TestEvent event1 = new TestEvent(1, 2);
		TestEvent event2 = new TestEvent(10, 20);

		assertEquals(3, event1.fireSync());
		assertEquals(30, event2.fireSync());
	}

	@Test
	public void testLocalEvent() {
		TestLocalEvent event1 = new TestLocalEvent(1, 2);
		TestLocalEvent event2 = new TestLocalEvent(10, 20);

		assertEquals(3, event1.fireSync());
		assertEquals(30, event2.fireSync());
	}

	@Test
	public void testLocalRequest() {
		TestLocalRequest event1 = new TestLocalRequest(1, 2);
		TestLocalRequest event2 = new TestLocalRequest(10, 20);

		assertEquals(3, event1.fireSync());
		assertEquals(30, event2.fireSync());
	}

	@Test
	public void testExternalEvent() {
		System.out.println(EXTERNAL_METHODS);
		assertEquals(1, EXTERNAL_METHODS.size());
	}

	@Test
	public void testUnregisterEvent() {
		EVENT_MANAGER.register(TestUnregisterEventListener.class);
		TestUnregisterEvent event1 = new TestUnregisterEvent(1, 2);
		assertEquals(3, event1.fireSync());

		EVENT_MANAGER.unregister(TestUnregisterEventListener.class);
		TestUnregisterEvent event2 = new TestUnregisterEvent(1, 2);
		assertEquals(0, event2.fireSync());
	}

	@Test
	public void testLongEvnet() throws InterruptedException, ExecutionException {
		LongLocalEvent event = new LongLocalEvent();

		CompletableFuture<Boolean> completableFuture = event.fireAsync(true);

		assertEquals(false, event.getResult());

		Thread.sleep(1000);
		assertEquals(false, event.getResult());

		completableFuture.get();
		assertEquals(true, event.getResult());
	}


}
