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

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisTtlKeeper
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
@Slf4j
public class RedisTtlKeeper implements Executable, InitializingBean, DisposableBean {

	private static final long DEFAULT_TTL_CHECK_INTERVAL = 5L;
	private final RedisOperations<String, Object> redisOperations;
	private final Map<String, Long> expirations = new ConcurrentHashMap<String, Long>();

	public RedisTtlKeeper(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setDefaultSerializer(RedisSerializer.string());
		redisTemplate.afterPropertiesSet();
		this.redisOperations = redisTemplate;
	}

	private Timer timer;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.timer = ThreadUtils.scheduleWithFixedDelay(this, DEFAULT_TTL_CHECK_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public boolean execute() throws Throwable {
		if (expirations.isEmpty()) {
			return true;
		}
		String key;
		long expiration;
		for (Map.Entry<String, Long> entry : expirations.entrySet()) {
			key = entry.getKey();
			expiration = entry.getValue();
			if (redisOperations.hasKey(key)) {
				long ttl = redisOperations.getExpire(key);
				if (ttl <= DEFAULT_TTL_CHECK_INTERVAL || ttl <= expiration * 0.2) {
					redisOperations.expire(key, expiration, TimeUnit.SECONDS);
					if (log.isTraceEnabled()) {
						log.trace("Expire key '{}' in seconds: {}", key, expiration);
					}
				}
			}
		}
		return true;
	}

	public void watchKey(String key, long timeout, TimeUnit timeUnit) {
		Assert.hasNoText(key, "Null key");
		long expiration = DateUtils.converToSecond(timeout, timeUnit);
		Assert.lte(expiration, 0L, "Invalid timeout");
		if (!expirations.containsKey(key)) {
			if (redisOperations.hasKey(key)) {
				redisOperations.expire(key, expiration, TimeUnit.SECONDS);
				expirations.put(key, expiration);
				log.trace("Keep watching key: {}", key);
			}
		}
	}

	public boolean hasWatched(String key) {
		return expirations.containsKey(key);
	}

	public int countOfWatchedKey() {
		return expirations.size();
	}

	public void unwatchKey(String key) {
		expirations.remove(key);
		log.trace("No longer watching key: {}", key);
	}

	@Override
	public void destroy() throws Exception {
		if (timer != null) {
			timer.cancel();
		}
	}

}
