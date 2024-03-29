/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * RedisCountDownLatch
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class RedisCountDownLatch implements DistributedCountDownLatch, Executable {

	private static final String LATCH_NAME_PREFIX = "CountDownLatch:";
	private static final String ATTACHMENT_NAME_PREFIX = "CountDownLatch:Attachment:";
	private final String latchName;
	private final String attachmentName;
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisAtomicLong counter;
	private CountDownLatch latch;
	private Timer timer;

	public RedisCountDownLatch(String name, RedisTemplate<String, Object> redisTemplate) {
		this.latchName = LATCH_NAME_PREFIX + name;
		this.attachmentName = ATTACHMENT_NAME_PREFIX + name;
		this.redisTemplate = redisTemplate;
		this.counter = new RedisAtomicLong(latchName, redisTemplate.getConnectionFactory());
	}

	public RedisCountDownLatch(String name, RedisTemplate<String, Object> redisTemplate, long permits) {
		this(name, redisTemplate);
		
		long exists = redisTemplate.hasKey(attachmentName) ? redisTemplate.opsForList().size(attachmentName) : 0;
		this.counter.set(permits - exists);
		this.latch = new CountDownLatch(1);
		this.timer = ThreadUtils.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
	}

	@Override
	public Object[] await() {
		if (latch != null) {
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			try {
				List<Object> list = redisTemplate.opsForList().range(attachmentName, 0, -1);
				redisTemplate.delete(attachmentName);
				return list.toArray();
			} finally {
				destroy();
			}
		}
		return null;
	}

	@Override
	public Object[] await(long timeout, TimeUnit timeUnit) {
		if (latch != null) {
			try {
				latch.await(timeout, timeUnit);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			try {
				List<Object> list = redisTemplate.opsForList().range(attachmentName, 0, -1);
				redisTemplate.delete(attachmentName);
				return list.toArray();
			} finally {
				destroy();
			}
		}
		return null;
	}

	@Override
	public void countDown(Object attachment) {
		if (isLocked()) {
			redisTemplate.opsForList().leftPush(attachmentName, attachment);
			counter.decrementAndGet();
		}
	}

	@Override
	public boolean isLocked() {
		try {
			return counter.get() > 0;
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Override
	public void cancel() {
		counter.set(0);
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
		counter.expire(1, TimeUnit.SECONDS);
	}

	@Override
	public boolean execute() {
		boolean locked = isLocked();
		if (!locked) {
			latch.countDown();
		}
		return locked;
	}

	public String toString() {
		return "[RedisCountDownLatch] latchName: " + latchName;
	}

}
