/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.springdessert.reditools.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RedisProcessLock
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@SuppressWarnings("all")
public class RedisProcessLock implements ProcessLock, ApplicationListener<RedisKeyExpiredEvent> {

	public RedisProcessLock(String lockName, RedisConnectionFactory connectionFactory, int expiration, int maxPermits) {
		this.connectionFactory = connectionFactory;
		this.expiration = expiration;
		this.maxPermits = maxPermits;
		this.counter = initializeCounter(lockName);
		this.startTime = System.currentTimeMillis();
	}

	private final int maxPermits;
	private final RedisConnectionFactory connectionFactory;
	private final int expiration;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private RedisAtomicInteger counter;
	private long startTime;
	private boolean reused = true;
	private volatile boolean expired;

	protected RedisAtomicInteger initializeCounter(String lockName) {
		RedisAtomicInteger counter = new RedisAtomicInteger(lockName + ":counter", connectionFactory);
		counter.expire(expiration, TimeUnit.SECONDS);
		return counter;
	}

	public boolean isReused() {
		return reused;
	}

	public void setReused(boolean reused) {
		this.reused = reused;
	}

	public boolean isExpired() {
		return expired;
	}

	@Override
	public boolean acquire() {
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					return true;
				} else {
					try {
						condition.await();
					} catch (InterruptedException e) {
						break;
					}
				}
			} catch (RuntimeException e) {
				throw new ProcessLockException(e.getMessage(), e);
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	@Override
	public boolean acquire(long timeout, TimeUnit timeUnit) {
		final long begin = System.nanoTime();
		long elapsed;
		long nanosTimeout = TimeUnit.NANOSECONDS.convert(timeout, timeUnit);
		while (true) {
			lock.lock();
			try {
				if (counter.get() < maxPermits) {
					counter.incrementAndGet();
					return true;
				} else {
					if (nanosTimeout > 0) {
						try {
							condition.awaitNanos(nanosTimeout);
						} catch (InterruptedException e) {
							break;
						}
						elapsed = (System.nanoTime() - begin);
						nanosTimeout -= elapsed;
					} else {
						break;
					}
				}
			} catch (RuntimeException e) {
				throw new ProcessLockException(e.getMessage(), e);
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	@Override
	public boolean tryAcquire() {
		try {
			if (counter.get() < maxPermits) {
				counter.incrementAndGet();
				return true;
			}
		} catch (RuntimeException e) {
			throw new ProcessLockException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public long cons() {
		return maxPermits - availablePermits();
	}

	@Override
	public void release() {
		if (!isLocked()) {
			return;
		}
		lock.lock();
		try {
			condition.signalAll();
			if (counter.decrementAndGet() < 0) {
				counter.set(0);
			}
		} catch (RuntimeException e) {
			throw new ProcessLockException(e.getMessage(), e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isLocked() {
		try {
			return counter.get() > 0;
		} catch (RuntimeException e) {
			throw new ProcessLockException(e.getMessage(), e);
		}
	}

	@Override
	public long join() {
		while (isLocked()) {
			ThreadUtils.randomSleep(1000L);
		}
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public String getLockName() {
		return counter.getKey();
	}

	@Override
	public int getExpiration() {
		return expiration;
	}

	@Override
	public long availablePermits() {
		try {
			return maxPermits - counter.get();
		} catch (RuntimeException e) {
			throw new ProcessLockException(e.getMessage(), e);
		}
	}

	@Override
	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		if (reused) {
			lock.lock();
			try {
				final String expiredKey = new String(event.getSource(), CharsetUtils.UTF_8);
				if (expiredKey.equals(getLockName())) {
					this.counter = initializeCounter(getLockName());
					this.startTime = System.currentTimeMillis();
				}
			} finally {
				lock.unlock();
			}
		} else {
			expired = true;
		}
	}

}
