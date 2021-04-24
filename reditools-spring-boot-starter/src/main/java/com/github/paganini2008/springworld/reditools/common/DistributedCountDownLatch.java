package com.github.paganini2008.springworld.reditools.common;

import java.util.concurrent.TimeUnit;

/**
 * 
 * DistributedCountDownLatch
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface DistributedCountDownLatch {

	void countDown(Object attachment);

	Object[] await();

	Object[] await(long timeout, TimeUnit timeUnit);

	boolean isLocked();

	void cancel();

	void destroy();

}