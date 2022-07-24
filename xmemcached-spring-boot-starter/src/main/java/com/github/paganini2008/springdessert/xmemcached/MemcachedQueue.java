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
package com.github.paganini2008.springdessert.xmemcached;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.paganini2008.devtools.collection.MapUtils;

import lombok.Getter;
import net.rubyeye.xmemcached.Counter;

/**
 * 
 * MemcachedQueue
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public final class MemcachedQueue {

	private static final String PUSHING_KEY = ":pushing";
	private static final String POPPING_KEY = ":popping";

	private final MemcachedOperations operations;
	private final Map<String, QueueCounter> counters = new ConcurrentHashMap<String, QueueCounter>();

	public MemcachedQueue(MemcachedOperations operations) {
		this.operations = operations;
	}

	public boolean push(String key, int expiration, Object value) throws Exception {
		QueueCounter counter = MapUtils.get(counters, key, () -> {
			return new QueueCounter(key);
		});
		Counter pushing = counter.getPushing();
		Counter popping = counter.getPopping();
		if (popping.get() > pushing.get()) {
			pushing.set(popping.get());
		}
		String serialKey = key + ":" + pushing.incrementAndGet();
		return operations.set(serialKey, expiration, value);
	}

	public <T> T pop(String key, Class<T> requiredType) throws Exception {
		QueueCounter counter = MapUtils.get(counters, key, () -> {
			return new QueueCounter(key);
		});
		Counter pushing = counter.getPushing();
		Counter popping = counter.getPopping();
		if (pushing.get() <= popping.get()) {
			return null;
		}
		String serialKey = key + ":" + popping.incrementAndGet();
		T value;
		if ((value = operations.get(serialKey, requiredType)) != null) {
			operations.getClient().deleteWithNoReply(serialKey);
			return value;
		}
		return null;
	}

	@Getter
	class QueueCounter {

		private Counter pushing;
		private Counter popping;

		QueueCounter(String key) {
			pushing = operations.getClient().getCounter(key + PUSHING_KEY, 0);
			popping = operations.getClient().getCounter(key + POPPING_KEY, 0);
		}

	}

}
