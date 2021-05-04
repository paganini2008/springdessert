package com.github.paganini2008.springworld.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.paganini2008.springworld.jdbc.DaoListener;

/**
 * 
 * Update
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {

	String value();

	Class<? extends DaoListener>[] listeners() default {};

}
