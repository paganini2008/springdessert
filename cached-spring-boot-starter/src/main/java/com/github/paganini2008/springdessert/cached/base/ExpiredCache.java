package com.github.paganini2008.springdessert.cached.base;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 
 * ExpiredCache
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface ExpiredCache extends Cache {

	void expire(String key, long time, TimeUnit timeUnit);

	void expireAt(String key, Date deadline);

}
