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

import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * RedisListSlice
 *
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
public class RedisListSlice<T> implements ResultSetSlice<T> {

	private final String key;
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisListSlice(String key, RedisTemplate<String, Object> redisTemplate) {
		this.key = key;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public int rowCount() {
		Number number = redisTemplate.opsForList().size(key);
		return number != null ? number.intValue() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(int maxResults, int firstResult) {
		return (List<T>) redisTemplate.opsForList().range(key, firstResult, firstResult + maxResults - 1);
	}

}
