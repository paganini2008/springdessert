/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.github.paganini2008.springdessert.reditools.RedisComponentNames;

/**
 * 
 * RedisMessageConfig
 * 
 * @author Fred Feng
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisMessageConfig {

	@ConditionalOnMissingBean(name = RedisComponentNames.REDIS_SERIALIZER)
	@Bean(RedisComponentNames.REDIS_SERIALIZER)
	public RedisSerializer<Object> redisSerializer() {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		return jackson2JsonRedisSerializer;
	}

	@Bean(RedisComponentNames.REDIS_TEMPLATE)
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
			@Qualifier(RedisComponentNames.REDIS_SERIALIZER) RedisSerializer<Object> redisSerializer) {
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

	@ConditionalOnMissingBean
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	@Bean
	public KeyExpirationEventMessageListener keyExpirationEventMessageListener(
			@Qualifier(RedisComponentNames.REDIS_MESSAGE_LISTENER_CONTAINER) RedisMessageListenerContainer redisMessageListenerContainer) {
		KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(redisMessageListenerContainer);
		listener.setKeyspaceNotificationsConfigParameter("Ex");
		return listener;
	}

	@Bean(RedisComponentNames.REDIS_MESSAGE_EVENT_PUBLISHER)
	public RedisMessageEventPublisher redisMessageEventPublisher() {
		return new RedisMessageEventPublisher();
	}

	@ConditionalOnProperty(name = "spring.redis.messager.dispatcher.mode", havingValue = "pubsub", matchIfMissing = true)
	@Configuration(proxyBeanMethods = false)
	public static class PubSubModeConfig {

		@Value("${spring.redis.messager.pubsub.channel:messager-pubsub}")
		private String pubsubChannelKey;

		@Bean(RedisComponentNames.PUBSUB_REDIS_MESSAGE_DISPATCHER)
		public RedisMessageDispatcher pubSubRedisMessageDispatcher() {
			return new PubSubRedisMessageDispatcher();
		}

		@Bean(RedisComponentNames.REDIS_MESSAGE_PUBSUB_LISTENER)
		public MessageListenerAdapter redisMessagePubsubListener(
				@Qualifier(RedisComponentNames.REDIS_MESSAGE_EVENT_PUBLISHER) RedisMessageEventPublisher eventPublisher,
				@Qualifier(RedisComponentNames.REDIS_SERIALIZER) RedisSerializer<Object> redisSerializer) {
			MessageListenerAdapter adapter = new MessageListenerAdapter(eventPublisher, "doPubsub");
			adapter.setSerializer(redisSerializer);
			adapter.afterPropertiesSet();
			return adapter;
		}

		@Bean(RedisComponentNames.REDIS_MESSAGE_LISTENER_CONTAINER)
		public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
				@Qualifier(RedisComponentNames.REDIS_MESSAGE_PUBSUB_LISTENER) MessageListenerAdapter redisMessagePubsubListener) {
			RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
			redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
			redisMessageListenerContainer.addMessageListener(redisMessagePubsubListener, new ChannelTopic(pubsubChannelKey));
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
		@Bean(RedisComponentNames.QUEUE_REDIS_MESSAGE_DISPATCHER)
		public RedisMessageDispatcher queueRedisMessageDispatcher() {
			return new QueueRedisMessageDispatcher();
		}

		@Bean(RedisComponentNames.REDIS_MESSAGE_QUEUE_LISTENER)
		public MessageListenerAdapter redisMessageQueueListener(
				@Qualifier(RedisComponentNames.REDIS_MESSAGE_EVENT_PUBLISHER) RedisMessageEventPublisher eventPublisher,
				@Qualifier(RedisComponentNames.REDIS_SERIALIZER) RedisSerializer<Object> redisSerializer) {
			MessageListenerAdapter adapter = new MessageListenerAdapter(eventPublisher, "doQueue");
			adapter.setSerializer(redisSerializer);
			adapter.afterPropertiesSet();
			return adapter;
		}

		@Bean(RedisComponentNames.REDIS_MESSAGE_LISTENER_CONTAINER)
		public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
				@Qualifier(RedisComponentNames.REDIS_MESSAGE_QUEUE_LISTENER) MessageListenerAdapter redisMessageQueueListener) {
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

	@Bean(RedisComponentNames.REDIS_MESSAGE_EVENT_LISTENER)
	public RedisMessageEventListener redisMessageEventListener() {
		return new RedisMessageEventListener();
	}

	@Bean(RedisComponentNames.REDIS_MESSAGE_SENDER)
	public RedisMessageSender redisMessageSender() {
		return new RedisMessageSender();
	}

}
