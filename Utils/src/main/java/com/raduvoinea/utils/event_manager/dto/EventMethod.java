package com.raduvoinea.utils.event_manager.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.utils.event_manager.annotation.EventHandler;
import com.raduvoinea.utils.event_manager.exceptions.RuntimeEventException;
import com.raduvoinea.utils.lambda.ScheduleUtils;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class EventMethod {
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private final Method method;
	private final Object parentObject;
	private final EventHandler annotation;

	private final List<CompletableFuture<?>> pendingCompletableFutures = Collections.synchronizedList(new ArrayList<>());

	public EventMethod(Object parentObject, @NotNull Method method) {
		this.parentObject = parentObject;
		this.method = method;
		this.method.setAccessible(true);
		this.annotation = method.getAnnotation(EventHandler.class);
	}

	public void fire(Object event, boolean suppressExceptions, boolean isOffThread) {
		if (this.annotation.skipCancelled() &&
			event instanceof IEvent<?> iEvent &&
			iEvent.isCancelled()) {
			return;
		}

		if (!isOffThread && this.annotation.async()) {
			ScheduleUtils.runTaskAsync(() -> this.fireSync(event, suppressExceptions));
			return;
		}

		this.fireSync(event, suppressExceptions);
	}

	public void fireSync(Object event, boolean suppressExceptions) {
		try {
			method.invoke(parentObject, event);
		} catch (Exception error) {
			String eventData = this.serializeEventData(event);

			Logger.error(
				new MessageBuilder(
					"Error while invoking event method {method} from class {class} for event {event}\n" +
						"JSON (debug only json): {json}"
				)
					.parse("method", method.getName())
					.parse("class", method.getDeclaringClass().getName())
					.parse("event", event.getClass().getSimpleName())
					.parse("json", eventData)
					.parse()
			);

			if (suppressExceptions) {
				Logger.error(error);
			} else {
				throw new RuntimeEventException(error);
			}
		}
	}

	private String serializeEventData(Object event) {
		String eventData;

		try {
			eventData = GSON.toJson(event);
		} catch (Exception e) {
			eventData = event.toString();
			Logger.debug(new MessageBuilder("Failed to serialize event data to JSON, using toString() instead. Event class: {class}")
				.parse("class", event.getClass().getName())
			);
		}

		return eventData;
	}

	public static class Comparator implements java.util.Comparator<EventMethod> {

		private static Comparator instance;

		public static Comparator getInstance() {
			if (instance == null) {
				instance = new Comparator();
			}
			return instance;
		}

		@Override
		public int compare(@NotNull EventMethod object1, @NotNull EventMethod object2) {
			return object1.annotation.order() - object2.annotation.order();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EventMethod eventMethod)) {
			return false;
		}

		return eventMethod.method.equals(method);
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}
}