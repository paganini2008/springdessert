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
package com.github.paganini2008.springdessert.reditools.serializer;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 
 * FstRedisSerializer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class FstRedisSerializer<T> implements RedisSerializer<T> {

	private final FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();

	private final Class<T> requiredType;

	public FstRedisSerializer(Class<T> requiredType) {
		this.requiredType = requiredType;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return null;
		}
		return configuration.asByteArray(t);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return requiredType.cast(configuration.asObject(bytes));
	}

}
