package gg.mmorealms.loader.backend.common.annotation;

import gg.mmorealms.loader.common.dto.ServerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OnlyOn {

	ServerType[] servers() default {};

}
