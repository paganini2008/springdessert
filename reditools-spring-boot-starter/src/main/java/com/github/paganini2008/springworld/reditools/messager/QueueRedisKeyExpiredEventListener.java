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
package com.github.paganini2008.springworld.reditools.messager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;

import com.github.paganini2008.devtools.CharsetUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * QueueRedisKeyExpiredEventListener
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
@SuppressWarnings("all")
public class QueueRedisKeyExpiredEventListener implements ApplicationListener<RedisKeyExpiredEvent> {

	@Autowired
	private RedisMessageEventPublisher eventPublisher;

	@Value("${spring.redis.messager.ephemeral-key.namespace:ephemeral-message:}")
	private String namespace;

	public void onApplicationEvent(RedisKeyExpiredEvent event) {
		final String expiredKey = new String(event.getSource(), CharsetUtils.UTF_8);
		if (expiredKey.startsWith(namespace)) {
			if (log.isTraceEnabled()) {
				log.trace("Redis key '{}' is expired.", expiredKey);
			}
			RedisMessageEntity redisMessageEntity = getRedisMessageEntity(expiredKey);
			eventPublisher.doQueue(redisMessageEntity);
		}
	}

	protected RedisMessageEntity getRedisMessageEntity(String expiredKey) {
		return RedisMessageEntity.EMPTY;
	}

}
