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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 
 * GenericRedisTemplate
 *
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
@SuppressWarnings("all")
public class GenericRedisTemplate<T> extends RedisTemplate<String, T> {

	public GenericRedisTemplate(String key, Class<T> valueClass, RedisConnectionFactory redisConnectionFactory) {
		this(key, valueClass, redisConnectionFactory, null);
	}

	public GenericRedisTemplate(String key, Class<T> valueClass, RedisConnectionFactory redisConnectionFactory, T defaultValue) {
		this.key = key;
		setKeySerializer(RedisSerializer.string());
		setValueSerializer(new GenericToStringSerializer<T>(valueClass));
		setHashKeySerializer(RedisSerializer.string());
		setHashValueSerializer(new GenericToStringSerializer<T>(valueClass));
		setExposeConnection(true);
		setConnectionFactory(redisConnectionFactory);
		afterPropertiesSet();

		if (defaultValue != null) {
			setIfAbsent(defaultValue);
		}
	}

	private final String key;

	public void set(T value) {
		opsForValue().set(key, value);
	}

	public void setIfAbsent(T value) {
		if (!hasKey(key)) {
			opsForValue().set(key, value);
		}
	}

	public void set(T value, long expiration, TimeUnit timeUnit) {
		opsForValue().set(key, value, expiration, timeUnit);
	}

	public void setIfAbsent(T value, long expiration, TimeUnit timeUnit) {
		if (!hasKey(key)) {
			opsForValue().set(key, value, expiration, timeUnit);
		}
	}

	public T get() {
		return opsForValue().get(key);
	}

	public Long leftPushList(T value) {
		return opsForList().leftPush(key, value);
	}

	public Long leftPopList(Collection<T> c) {
		return opsForList().leftPushAll(key, c);
	}

	public Long rightPushList(T value) {
		return opsForList().rightPush(key, value);
	}

	public Long rightPushList(Collection<T> c) {
		return opsForList().rightPushAll(key, c);
	}

	public T leftPopList(String key) {
		return opsForList().leftPop(key);
	}

	public T rightPopList(String key) {
		return opsForList().rightPop(key);
	}

	public T indexList(long index) {
		return opsForList().index(key, index);
	}

	public Long removeList(long count, T value) {
		return opsForList().remove(key, count, value);
	}

	public List<T> rangeList(long start, long end) {
		return opsForList().range(key, start, end);
	}

	public long sizeList() {
		Long l = opsForList().size(key);
		return l != null ? l.longValue() : 0;
	}

	public void putHash(String hashKey, T value) {
		opsForHash().put(key, hashKey, value);
	}

	public Boolean putHashIfAbsent(String hashKey, T value) {
		return opsForHash().putIfAbsent(key, hashKey, value);
	}

	public T getHash(String hashKey) {
		return (T) opsForHash().get(key, hashKey);
	}

	public boolean hasHashKey(String hashKey) {
		return opsForHash().hasKey(key, hashKey);
	}

	public List<T> multiGetHash(String... hashKeys) {
		return (List<T>) opsForHash().multiGet(key, Arrays.asList(hashKeys));
	}

	public Long deleteHash(String... hashKeys) {
		return opsForHash().delete(key, hashKeys);
	}

	public long sizeHash() {
		return opsForHash().size(key);
	}

}
