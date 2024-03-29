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
package com.github.paganini2008.springdessert.xmemcached.serializer;

import org.nustaq.serialization.FSTConfiguration;

/**
 * 
 * FstMemcachedSerializer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class FstMemcachedSerializer implements MemcachedSerializer {

	private final FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();

	@Override
	public byte[] serialize(Object object) throws Exception {
		if (object == null) {
			return null;
		}
		return configuration.asByteArray(object);
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> requiredType) throws Exception {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return requiredType.cast(configuration.asObject(bytes));
	}

}
