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
package com.github.paganini2008.springdessert.xmemcached;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * 
 * MemcachedOperations
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface MemcachedOperations {

	default boolean set(String key, Object value) throws Exception {
		return set(key, 0, value);
	}

	boolean set(String key, int expiration, Object value) throws Exception;

	<T> T get(String key, Class<T> requiredType) throws Exception;

	boolean delete(String key) throws Exception;

	boolean push(String key, int expiration, Object value) throws Exception;

	<T> T pop(String key, Class<T> requiredType) throws Exception;

	MemcachedClient getClient();

}