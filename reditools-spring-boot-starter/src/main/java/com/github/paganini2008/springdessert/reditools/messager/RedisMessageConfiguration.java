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

import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.PUBSUB_REDIS_MESSAGE_DISPATCHER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.QUEUE_REDIS_MESSAGE_DISPATCHER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_MESSAGE_EVENT_LISTENER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_MESSAGE_EVENT_PUBLISHER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_MESSAGE_LISTENER_CONTAINER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_MESSAGE_PUBSUB_LISTENER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_MESSAGE_QUEUE_LISTENER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_MESSAGE_SENDER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_SERIALIZER;
import static com.github.paganini2008.springdessert.reditools.RedisComponentNames.REDIS_TEMPLATE;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * 
 * RedisMessageConfiguration
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisMessageConfiguration {

	@ConditionalOnMissingBean(name = REDIS_SERIALIZER)
	@Bean(REDIS_SERIALIZER)
	public RedisSerializer<Object> redisSerializer() {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		return jackson2JsonRedisSerializer;
	}

	@ConditionalOnMissingBean(name = REDIS_TEMPLATE)
	@Bean(name = REDIS_TEMPLATE)
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
			@Qualifier(REDIS_SERIALIZER) RedisSerializer<Object> redisSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(redisSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(redisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
			@Qualifier(REDIS_MESSAGE_LISTENER_CONTAINER) RedisMessageListenerContainer redisMessageListenerContainer) {
		KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
		listener.setKeyspaceNotificationsConfigParameter("Ex");
		return listener;
	}

	@Bean(REDIS_MESSAGE_EVENT_PUBLISHER)
	public RedisMessageEventPublisher redisMessageEventPublisher() {
		return new RedisMessageEventPublisher();
	}

	@ConditionalOnProperty(name = "spring.redis.messager.dispatcher.mode", havingValue = "pubsub", matchIfMissing = true)
	@Configuration(proxyBeanMethods = false)
	public static class PubSubModeConfig {

		@Value("${spring.redis.messager.pubsub.channel:messager-pubsub}")
		private String pubsubChannelKey;

		@Bean(PUBSUB_REDIS_MESSAGE_DISPATCHER)
		public RedisMessageDispatcher pubSubRedisMessageDispatcher() {
			return new PubSubRedisMessageDispatcher();
		}

		@Bean(REDIS_MESSAGE_PUBSUB_LISTENER)
		public MessageListenerAdapter redisMessagePubsubListener(
				@Qualifier(REDIS_MESSAGE_EVENT_PUBLISHER) RedisMessageEventPublisher eventPublisher,
				@Qualifier(REDIS_SERIALIZER) RedisSerializer<Object> redisSerializer) {
			MessageListenerAdapter adapter = new MessageListenerAdapter(eventPublisher, "doPubsub");
			adapter.setSerializer(redisSerializer);
			adapter.afterPropertiesSet();
			return adapter;
		}

		@Bean(REDIS_MESSAGE_LISTENER_CONTAINER)
		public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
				@Qualifier(REDIS_MESSAGE_PUBSUB_LISTENER) MessageListenerAdapter messageListener) {
			RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
			redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
			redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(pubsubChannelKey));
			return redisMessageListenerContainer;
		}

		@Bean
		public PubSubRedisKeyExpiredEventListener pubSubRedisKeyExpiredEventListener() {
			return new PubSubRedisKeyExpiredEventListener();
		}
	}

	@ConditionalOnProperty(name = "spring.redis.messager.dispatcher.mode", havingValue = "queue")
	@Configuration(proxyBeanMethods = false)
	public static class QueueModeConfig {

		@Value("${spring.redis.messager.queue.channel:messager-queue}")
		private String queueChannelKey;

		@ConditionalOnProperty(name = "spring.redis.messager.dispatcher.mode", havingValue = "queue")
		@Bean(QUEUE_REDIS_MESSAGE_DISPATCHER)
		public RedisMessageDispatcher queueRedisMessageDispatcher() {
			return new QueueRedisMessageDispatcher();
		}

		@Bean(REDIS_MESSAGE_QUEUE_LISTENER)
		public MessageListenerAdapter redisMessageQueueListener(
				@Qualifier(REDIS_MESSAGE_EVENT_PUBLISHER) RedisMessageEventPublisher eventPublisher,
				@Qualifier(REDIS_SERIALIZER) RedisSerializer<Object> redisSerializer) {
			MessageListenerAdapter adapter = new MessageListenerAdapter(eventPublisher, "doQueue");
			adapter.setSerializer(redisSerializer);
			adapter.afterPropertiesSet();
			return adapter;
		}

		@Bean(REDIS_MESSAGE_LISTENER_CONTAINER)
		public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
				@Qualifier(REDIS_MESSAGE_QUEUE_LISTENER) MessageListenerAdapter redisMessageQueueListener) {
			RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
			redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
			redisMessageListenerContainer.addMessageListener(redisMessageQueueListener, new ChannelTopic(queueChannelKey));
			return redisMessageListenerContainer;
		}

		@Bean
		public QueueRedisKeyExpiredEventListener queueRedisKeyExpiredEventListener() {
			return new QueueRedisKeyExpiredEventListener();
		}
	}

	@Bean(REDIS_MESSAGE_EVENT_LISTENER)
	public RedisMessageEventListener redisMessageEventListener() {
		return new RedisMessageEventListener();
	}

	@Bean(REDIS_MESSAGE_SENDER)
	public RedisMessageSender redisMessageSender() {
		return new RedisMessageSender();
	}

}
