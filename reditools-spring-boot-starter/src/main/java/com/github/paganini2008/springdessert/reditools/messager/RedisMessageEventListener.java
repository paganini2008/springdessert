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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.MapUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RedisMessageEventListener
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
@Slf4j
public class RedisMessageEventListener implements ApplicationListener<RedisMessageEvent> {

	private final ConcurrentMap<String, Map<String, RedisMessageHandler>> channelHandlers = new ConcurrentHashMap<String, Map<String, RedisMessageHandler>>();
	private final ConcurrentMap<String, Map<String, RedisMessageHandler>> channelPatternHandlers = new ConcurrentHashMap<String, Map<String, RedisMessageHandler>>();

	public void onApplicationEvent(RedisMessageEvent event) {
		final String channel = event.getChannel();
		final Object message = event.getMessage();
		Map<String, RedisMessageHandler> handlers = channelHandlers.get(channel);
		if (handlers != null && handlers.size() > 0) {
			RedisMessageHandler handler;
			for (Map.Entry<String, RedisMessageHandler> entry : handlers.entrySet()) {
				handler = entry.getValue();
				try {
					handler.onMessage(channel, message);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				if (!handler.isRepeatable()) {
					handlers.remove(entry.getKey());
				}
			}
		}
		for (String keyPattern : channelPatternHandlers.keySet()) {
			if (matchesChannel(keyPattern, channel)) {
				handlers = channelPatternHandlers.get(keyPattern);
				if (handlers != null && handlers.size() > 0) {
					RedisMessageHandler handler;
					for (Map.Entry<String, RedisMessageHandler> entry : handlers.entrySet()) {
						handler = entry.getValue();
						try {
							handler.onMessage(channel, message);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
						if (!handler.isRepeatable()) {
							handlers.remove(entry.getKey());
						}
					}
				}
			}
		}
	}

	private static boolean matchesChannel(String keyPattern, String channel) {
		if (StringUtils.isBlank(channel)) {
			return false;
		}
		String key;
		int index = keyPattern.lastIndexOf('*');
		if (index == keyPattern.length() - 1) {
			key = keyPattern.substring(0, index);
			return channel.startsWith(key);
		} else if (index == 0) {
			key = keyPattern.substring(1);
			return channel.endsWith(key);
		} else {
			String[] args = keyPattern.split("\\*");
			for (String arg : args) {
				if (!channel.contains(arg)) {
					return false;
				}
			}
			return true;
		}
	}

	public void removeHandler(String beanName) {
		for (Map<String, RedisMessageHandler> handlers : channelHandlers.values()) {
			handlers.remove(beanName);
		}
		for (Map<String, RedisMessageHandler> handlers : channelPatternHandlers.values()) {
			handlers.remove(beanName);
		}
	}

	public void addHandler(String beanName, RedisMessageHandler handler) {
		Map<String, RedisMessageHandler> handlers;
		String channel = handler.getChannel();
		if (channel.contains("*")) {
			channel = getCheckedChannel(channel);
			handlers = MapUtils.get(channelPatternHandlers, channel, () -> {
				return new ConcurrentHashMap<String, RedisMessageHandler>();
			});
		} else {
			handlers = MapUtils.get(channelHandlers, channel, () -> {
				return new ConcurrentHashMap<String, RedisMessageHandler>();
			});
		}
		handlers.putIfAbsent(beanName, handler);
	}

	private String getCheckedChannel(String channel) {
		return channel.replaceAll("[\\*]+", "*");
	}

}
