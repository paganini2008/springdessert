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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * RedisHashSlice
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public class RedisHashSlice<T> implements ResultSetSlice<T> {

	private final String key;
	private final RedisTemplate<String, Object> redisTemplate;

	public RedisHashSlice(String key, RedisTemplate<String, Object> redisTemplate) {
		this.key = key;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public int rowCount() {
		Number number = redisTemplate.opsForHash().size(key);
		return number != null ? number.intValue() : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(int maxResults, int firstResult) {
		List<T> dataList = new ArrayList<T>();
		Set<Object> hashKeys = redisTemplate.opsForHash().keys(key);
		List<Object> hashKeyList = ListUtils.slice(new ArrayList<Object>(hashKeys), maxResults, firstResult);
		for (Object hashKey : hashKeyList) {
			dataList.add((T) redisTemplate.opsForHash().get(key, hashKey));
		}
		return dataList;
	}

}
