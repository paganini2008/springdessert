package com.github.paganini2008.springdessert.cached;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Cached
 *
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cached {

	String ref() default "";
	
	String value() default "";
	
	int expire() default -1;
	
}
