package com.github.paganini2008.springworld.fastjpa.support;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * Ref
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface Ref {

	Class<? extends InjectionHandler> using() default RefInjectionHandler.class;

	String targetProperty();

}
