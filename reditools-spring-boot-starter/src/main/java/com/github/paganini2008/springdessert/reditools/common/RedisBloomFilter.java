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

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.github.paganini2008.devtools.CharsetUtils;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;

/**
 * 
 * RedisBloomFilter
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public class RedisBloomFilter {

	private final String key;
	private final int numHashFunctions;
	private final int bitSize;
	private final Funnel<CharSequence> funnel;
	private final RedisOperations<String, String> redisOperations;

	public RedisBloomFilter(String key, int expectedInsertions, double fpp, RedisConnectionFactory redisConnectionFactory) {
		this.key = key;
		this.funnel = Funnels.stringFunnel(CharsetUtils.UTF_8);
		this.bitSize = optimalNumOfBits(expectedInsertions, fpp);
		this.numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, bitSize);
		this.redisOperations = new StringRedisTemplate(redisConnectionFactory);
	}

	public void put(String content) {
		int[] offset = murmurHashOffset(content);
		for (int i : offset) {
			redisOperations.opsForValue().setBit(key, i, true);
		}
	}

	public boolean mightContain(String content) {
		int[] offset = murmurHashOffset(content);
		for (int i : offset) {
			if (!redisOperations.opsForValue().getBit(key, i)) {
				return false;
			}
		}
		return true;
	}

	int[] murmurHashOffset(CharSequence value) {
		int[] offset = new int[numHashFunctions];
		long hash64 = Hashing.murmur3_128().hashObject(value, funnel).asLong();
		int hash1 = (int) hash64;
		int hash2 = (int) (hash64 >>> 32);
		for (int i = 1; i <= numHashFunctions; i++) {
			int nextHash = hash1 + i * hash2;
			if (nextHash < 0) {
				nextHash = ~nextHash;
			}
			offset[i - 1] = nextHash % bitSize;
		}
		return offset;
	}

	static int optimalNumOfBits(long n, double p) {
		if (p == 0) {
			p = Double.MIN_VALUE;
		}
		return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
	}

	static int optimalNumOfHashFunctions(long n, long m) {
		return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
	}

}
