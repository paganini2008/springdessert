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
package com.github.paganini2008.springdessert.reditools.common;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 
 * RedisCalulation
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class RedisCalulation {

	private final RedisOperations<String, Long> longOperations;
	private final RedisOperations<String, Double> doubleOperations;

	public RedisCalulation(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Long> longOperations = new RedisTemplate<>();
		longOperations.setKeySerializer(RedisSerializer.string());
		longOperations.setValueSerializer(new GenericToStringSerializer<>(Long.class));
		longOperations.setExposeConnection(true);
		longOperations.setConnectionFactory(connectionFactory);
		longOperations.afterPropertiesSet();
		this.longOperations = longOperations;

		RedisTemplate<String, Double> doubleOperations = new RedisTemplate<>();
		doubleOperations.setKeySerializer(RedisSerializer.string());
		doubleOperations.setValueSerializer(new GenericToStringSerializer<>(Double.class));
		doubleOperations.setExposeConnection(true);
		doubleOperations.setConnectionFactory(connectionFactory);
		longOperations.afterPropertiesSet();
		this.doubleOperations = doubleOperations;
	}

	public void setLong(String key, long newValue) {
		longOperations.opsForValue().set(key, newValue);
	}

	public long getLong(String key) {
		Long l = longOperations.opsForValue().get(key);
		return valueOf(l);
	}

	public long getAndSetLong(String key, long newValue) {
		Long l = longOperations.opsForValue().getAndSet(key, newValue);
		return valueOf(l);
	}

	public long incrementAndGetLong(String key) {
		Long l = longOperations.opsForValue().increment(key, 1);
		return valueOf(l);
	}

	public long getLongAndIncrement(String key) {
		return incrementAndGetLong(key) - 1;
	}

	public long decrementAndGetLong(String key) {
		Long l = longOperations.opsForValue().increment(key, -1);
		return valueOf(l);
	}

	public long getLongAndDecrement(String key) {
		return decrementAndGetLong(key) + 1;
	}

	public long getLongAndAdd(String key, long delta) {
		return addAndGetLong(key, delta) - delta;
	}

	public long addAndGetLong(String key, long delta) {
		Long l = longOperations.opsForValue().increment(key, delta);
		return valueOf(l);
	}

	public void setDouble(String key, double newValue) {
		doubleOperations.opsForValue().set(key, newValue);
	}

	public double getDouble(String key) {
		Double d = doubleOperations.opsForValue().get(key);
		return valueOf(d);
	}

	public double getDoubleAndSet(String key, double newValue) {
		Double d = doubleOperations.opsForValue().getAndSet(key, newValue);
		return valueOf(d);
	}

	public double getDoubleAndAdd(String key, double delta) {
		return addAndGetDouble(key, delta) - delta;
	}

	public double addAndGetDouble(String key, double delta) {
		Double d = doubleOperations.opsForValue().increment(key, delta);
		return valueOf(d);
	}

	private long valueOf(Long l) {
		return l != null ? l.longValue() : 0L;
	}

	private double valueOf(Double d) {
		return d != null ? d.doubleValue() : 0d;
	}

}
