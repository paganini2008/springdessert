/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.springworld.reditools.common;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.date.DateUtils;

/**
 * 
 * TimeBasedIdGenerator
 *
 * @author Fred Feng
 * @since 1.0
 */
public class TimeBasedIdGenerator implements IdGenerator {

	private static final String defaultDatePattern = "yyyyMMddHHmmss";
	private static final int maxConcurrency = 100000;
	private final String keyPrefix;
	private final RedisConnectionFactory connectionFactory;
	private final Map<String, RedisAtomicLong> cache;

	public TimeBasedIdGenerator(RedisConnectionFactory connectionFactory) {
		this("id:", connectionFactory);
	}

	public TimeBasedIdGenerator(String keyPrefix, RedisConnectionFactory connectionFactory) {
		this.keyPrefix = keyPrefix;
		this.connectionFactory = connectionFactory;
		this.cache = new LruMap<String, RedisAtomicLong>(60);
	}

	@Override
	public long generateId() {
		final String timestamp = DateUtils.format(System.currentTimeMillis(), defaultDatePattern);
		RedisAtomicLong counter = MapUtils.get(cache, timestamp, () -> {
			RedisAtomicLong l = new RedisAtomicLong(keyPrefix + ":" + timestamp, connectionFactory);
			l.expire(60, TimeUnit.SECONDS);
			return l;
		});
		return Long.parseLong(timestamp) * maxConcurrency + counter.incrementAndGet();
	}

}
