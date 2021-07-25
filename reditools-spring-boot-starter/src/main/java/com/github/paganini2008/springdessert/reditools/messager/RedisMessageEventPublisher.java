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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.springdessert.reditools.RedisComponentNames;

/**
 * 
 * RedisMessageEventPublisher
 *
 * @author Fred Feng
 *
 * @since 1.0
 */
public class RedisMessageEventPublisher implements ApplicationContextAware {

	@Value("${spring.redis.messager.queue:messager-queue}")
	private String queueKey;

	@Autowired
	@Qualifier(RedisComponentNames.REDIS_TEMPLATE)
	private RedisTemplate<String, Object> redisTemplate;

	public void doQueue(RedisMessageEntity entity) {
		RedisMessageEntity messageEntity = (RedisMessageEntity) redisTemplate.opsForList().leftPop(queueKey);
		if (messageEntity != null) {
			applicationContext.publishEvent(new RedisMessageEvent(messageEntity));
		}
	}

	public void doPubsub(RedisMessageEntity messageEntity) {
		applicationContext.publishEvent(new RedisMessageEvent(messageEntity));
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
