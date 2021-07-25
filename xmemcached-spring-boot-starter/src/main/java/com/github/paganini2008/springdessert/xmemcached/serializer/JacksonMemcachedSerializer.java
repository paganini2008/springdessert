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
package com.github.paganini2008.springdessert.xmemcached.serializer;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paganini2008.devtools.io.SerializationException;

/**
 * 
 * JacksonMemcachedSerializer
 *
 * @author Fred Feng
 * @version 1.0
 */
public class JacksonMemcachedSerializer implements MemcachedSerializer {

	private final ObjectMapper objectMapper;

	{
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsBytes(object);
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> requiredType) throws Exception {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			return this.objectMapper.readValue(bytes, 0, bytes.length, requiredType);
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

}
