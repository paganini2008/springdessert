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
package com.github.paganini2008.springdessert.reditools.messager;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.springdessert.reditools.RedisComponentNames;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * PubSubRedisKeyExpiredEventListener
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
@SuppressWarnings("all")
public class PubSubRedisKeyExpiredEventListener implements ApplicationListener<RedisKeyExpiredEvent> {

	@Autowired
	private RedisMessageEventPublisher eventPublisher;

	@Qualifier(RedisComponentNames.REDIS_TEMPLATE)
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${spring.redis.messager.ephemeral-key.namespace:ephemeral-message:}")
	private String namespace;

	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource(), CharsetUtils.UTF_8);
		if (expiredKey.startsWith(namespace)) {
			if (log.isTraceEnabled()) {
				log.trace("Redis key '{}' is expired.", expiredKey);
			}
			RedisMessageEntity redisMessageEntity = getRedisMessageEntity(expiredKey);
			eventPublisher.doPubsub(redisMessageEntity);
		}
	}

	protected RedisMessageEntity getRedisMessageEntity(String expiredKey) {
		final String key = PubSubRedisMessageDispatcher.EXPIRED_KEY_PREFIX + expiredKey;
		if (redisTemplate.hasKey(key)) {
			RedisMessageEntity entity = (RedisMessageEntity) redisTemplate.opsForValue().get(key);
			redisTemplate.expire(key, 60, TimeUnit.SECONDS);
			return entity;
		}
		return RedisMessageEntity.EMPTY;
	}

}
