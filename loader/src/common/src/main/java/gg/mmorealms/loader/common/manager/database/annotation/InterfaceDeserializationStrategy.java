package gg.mmorealms.loader.common.manager.database.annotation;

import gg.mmorealms.loader.common.manager.database.enums.InterfaceDeserializationStrategyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InterfaceDeserializationStrategy {

	InterfaceDeserializationStrategyType type();

}