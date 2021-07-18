/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * 
 * RedisCounter
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class RedisCounter {

	private final RedisAtomicLong longValue;

	public RedisCounter(String name, RedisOperations<String, Long> redisOperations) {
		this.longValue = new RedisAtomicLong(name, redisOperations, 0L);
	}

	public RedisCounter(String name, RedisConnectionFactory connectionFactory) {
		this.longValue = new RedisAtomicLong(name, connectionFactory, 0L);
	}

	public void expire(long timeout, TimeUnit timeUnit) {
		longValue.expire(timeout, timeUnit);
	}

	public void expireAt(Date date) {
		longValue.expireAt(date);
	}

	public void keepAlive(TtlKeeper keeper, int timeout, TimeUnit timeUnit) {
		keeper.keepAlive(longValue.getKey(), timeout, 1, timeUnit);
	}

	public void set(long newValue) {
		longValue.set(newValue);
	}

	public long get() {
		return longValue.get();
	}

	public long getAndIncrement() {
		return longValue.getAndIncrement();
	}

	public long getAndDecrement() {
		return longValue.getAndDecrement();
	}

	public long incrementAndGet() {
		return longValue.incrementAndGet();
	}

	public long decrementAndGet() {
		return longValue.decrementAndGet();
	}

	public long getAndAdd(long delta) {
		return longValue.getAndAdd(delta);
	}

	public long getAndSet(long newValue) {
		return longValue.getAndSet(newValue);
	}

	public long addAndGet(long delta) {
		return longValue.addAndGet(delta);
	}

	public String getKey() {
		return longValue.getKey();
	}

	public String toString() {
		return longValue.toString();
	}

	public void destroy() {
		longValue.expire(1, TimeUnit.SECONDS);
	}

}
