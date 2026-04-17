package com.raduvoinea.utils.event_manager.annotation;

import com.raduvoinea.utils.event_manager.dto.IEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

	int order() default 0;

	boolean ignore() default false;

	/**
	 * Overwrites the default call chain for {@link com.raduvoinea.utils.event_manager.EventManager#fireSync(IEvent, boolean)} and makes the method always be called async
	 * **WARNING** This makes the method not be able to cause any modifications to the event state
	 * **WARNING** Settings `async=false` when the event is fired with {@link com.raduvoinea.utils.event_manager.EventManager#fireAsync(IEvent, boolean, long)} will break the chain of execution
	 */
	boolean async() default false;

	boolean skipCancelled() default true;

}
