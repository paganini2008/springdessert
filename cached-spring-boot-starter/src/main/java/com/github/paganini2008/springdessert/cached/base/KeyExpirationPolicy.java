package com.github.paganini2008.springdessert.cached.base;

/**
 * 
 * KeyExpirationPolicy
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface KeyExpirationPolicy {

	default void onSort(String key, Cache cache) {
	}

	default void onDelete(String key, Cache cache) {
	}

	default void onClear(Cache cache) {
	}

}
