package com.github.paganini2008.springdessert.cached.base;

/**
 * 
 * RemovalListener
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface RemovalListener {

	default void onRemoval(RemovalNotification removalNotification) {
	}

}
