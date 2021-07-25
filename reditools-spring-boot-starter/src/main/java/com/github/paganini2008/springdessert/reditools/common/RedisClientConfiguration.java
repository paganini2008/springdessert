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
package com.github.paganini2008.springdessert.reditools.common;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.paganini2008.devtools.StringUtils;

import io.lettuce.core.RedisClient;
import lombok.Setter;

/**
 * 
 * RedisClientConfiguration
 *
 * @author Fred Feng
 * @version 1.0
 */
@SuppressWarnings("all")
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@ConditionalOnMissingBean(RedisConnectionFactory.class)
public class RedisClientConfiguration {

	private String host;
	private String password;
	private int port;
	private int database;

	@ConditionalOnClass({ GenericObjectPool.class, RedisClient.class })
	@Bean("redisConnectionFactory")
	public RedisConnectionFactory lettuceRedisConnectionFactory(RedisConfiguration redisConfiguration,
			GenericObjectPoolConfig redisPoolConfig) {
		LettuceClientConfiguration redisClientConfiguration = LettucePoolingClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(60000)).shutdownTimeout(Duration.ofMillis(60000)).poolConfig(redisPoolConfig).build();
		if (redisConfiguration instanceof RedisStandaloneConfiguration) {
			return new LettuceConnectionFactory((RedisStandaloneConfiguration) redisConfiguration, redisClientConfiguration);
		} else if (redisConfiguration instanceof RedisSentinelConfiguration) {
			return new LettuceConnectionFactory((RedisSentinelConfiguration) redisConfiguration, redisClientConfiguration);
		} else if (redisConfiguration instanceof RedisClusterConfiguration) {
			return new LettuceConnectionFactory((RedisClusterConfiguration) redisConfiguration, redisClientConfiguration);
		}
		throw new UnsupportedOperationException("Create LettuceConnectionFactory");
	}

	@ConditionalOnMissingBean
	@Bean
	public GenericObjectPoolConfig redisPoolConfig() {
		GenericObjectPoolConfig redisPoolConfig = new GenericObjectPoolConfig();
		redisPoolConfig.setMinIdle(1);
		redisPoolConfig.setMaxIdle(10);
		redisPoolConfig.setMaxTotal(200);
		redisPoolConfig.setMaxWaitMillis(-1);
		redisPoolConfig.setTestWhileIdle(true);
		redisPoolConfig.setMinEvictableIdleTimeMillis(60000);
		redisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
		redisPoolConfig.setNumTestsPerEvictionRun(-1);
		return redisPoolConfig;
	}

	@ConditionalOnMissingBean
	@Bean
	public RedisConfiguration redisConfiguration() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setDatabase(database);
		if (StringUtils.isNotBlank(password)) {
			redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		}
		return redisStandaloneConfiguration;
	}

	@ConditionalOnMissingBean(name = "stringRedisTemplate")
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	@ConditionalOnMissingBean(name = "redisTemplate")
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

}
