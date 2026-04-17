package com.raduvoinea.commandmanager.common.annotation;

import com.raduvoinea.commandmanager.common.command.CommonCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

	String[] aliases();

	String[] arguments() default {};

	OnlyFor onlyFor() default OnlyFor.BOTH;

	Class<? extends CommonCommand> parent() default CommonCommand.class;

	enum OnlyFor {
		PLAYERS,
		CONSOLE,
		BOTH
	}
}
