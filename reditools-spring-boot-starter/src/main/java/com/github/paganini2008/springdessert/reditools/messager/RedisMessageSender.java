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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * 
 * RedisMessageSender
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class RedisMessageSender implements BeanPostProcessor {

	@Value("${spring.redis.messager.ephemeral-key.namespace:ephemeral-message:}")
	private String namespace;

	@Autowired
	private RedisMessageDispatcher redisMessageDispather;

	@Autowired
	private RedisMessageEventListener redisMessageListener;

	public void sendMessage(RedisMessageEntity messageEntity) {
		redisMessageDispather.dispatch(messageEntity);
	}

	public void sendMessage(String channel, Object message) {
		sendMessage(createEntity(channel, message));
	}

	private RedisMessageEntity createEntity(String channel, Object message) {
		return RedisMessageEntity.of(channel, message);
	}

	public void sendEphemeralMessage(String channel, Object message, long delay, TimeUnit timeUnit) {
		final String expiredKey = namespace + channel;
		RedisMessageEntity messageEntity = createEntity(channel, message);
		redisMessageDispather.expire(expiredKey, messageEntity, delay, timeUnit);
	}

	public void subscribeChannel(String beanName, RedisMessageHandler messageHandler) {
		redisMessageListener.addHandler(beanName, messageHandler);
	}

	public void unsubscribeChannel(String beanName) {
		redisMessageListener.removeHandler(beanName);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof RedisMessageHandler) {
			RedisMessageHandler messageHandler = (RedisMessageHandler) bean;
			subscribeChannel(beanName, messageHandler);
		} else if (bean.getClass().isAnnotationPresent(MessageHandler.class)) {
			MessageHandler annotation = bean.getClass().getAnnotation(MessageHandler.class);
			subscribeChannel(beanName, new ReflectiveRedisMessageHandler(annotation, bean));
		}
		return bean;
	}

	/**
	 * 
	 * ReflectiveRedisMessageHandler
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class ReflectiveRedisMessageHandler implements RedisMessageHandler {

		private final MessageHandler annotation;
		private final Object targetBean;

		ReflectiveRedisMessageHandler(MessageHandler annotation, Object targetBean) {
			this.annotation = annotation;
			this.targetBean = targetBean;
		}

		@Override
		public String getChannel() {
			return annotation.value();
		}

		@Override
		public void onMessage(String channel, Object message) {
			MethodUtils.invokeMethodsWithAnnotation(targetBean, OnMessage.class, channel, message);
		}

		@Override
		public boolean isRepeatable() {
			return annotation.repeatable();
		}

	}

}
