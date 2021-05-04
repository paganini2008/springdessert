package com.github.paganini2008.springworld.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.paganini2008.springworld.jdbc.DaoListener;

/**
 * 
 * Get
 *
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Get {

	String value();

	boolean javaType() default false;

	Class<? extends DaoListener>[] listeners() default {};

}
