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
package com.github.paganini2008.springdessert.reditools.messager;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.springdessert.reditools.RedisComponentNames;

/**
 * 
 * PubSubRedisMessageDispatcher
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class PubSubRedisMessageDispatcher implements RedisMessageDispatcher {

	static final String EXPIRED_KEY_PREFIX = "__";

	@Value("${spring.redis.messager.pubsub.channel:messager-pubsub}")
	private String pubsubChannelKey;

	@Autowired
	@Qualifier(RedisComponentNames.REDIS_TEMPLATE)
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void dispatch(RedisMessageEntity messageEntity) {
		redisTemplate.convertAndSend(this.pubsubChannelKey, messageEntity);
	}

	@Override
	public void expire(String expiredKey, RedisMessageEntity messageEntity, long delay, TimeUnit timeUnit) {
		final String key = EXPIRED_KEY_PREFIX + expiredKey;
		redisTemplate.opsForValue().set(key, messageEntity);
		redisTemplate.opsForValue().set(expiredKey, messageEntity, delay, timeUnit);
	}

}
