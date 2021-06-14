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
package com.github.paganini2008.springdessert.reditools.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.springdessert.reditools.RedisComponentNames;

/**
 * 
 * RedisKeepAliveResolver
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class RedisKeepAliveResolver implements Executable, ApplicationListener<ContextRefreshedEvent>, BeanPostProcessor, DisposableBean {

	private static final int DEFAULT_CHECKED_INTERVAL = 3;
	private final List<ConnectionFailureHandler> listeners = new CopyOnWriteArrayList<ConnectionFailureHandler>();

	@Qualifier(RedisComponentNames.REDIS_TEMPLATE)
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private Timer timer;

	public void addListener(ConnectionFailureHandler listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeListener(ConnectionFailureHandler listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		timer = ThreadUtils.scheduleWithFixedDelay(this, DEFAULT_CHECKED_INTERVAL, TimeUnit.SECONDS);
	}

	public String ping() {
		return redisTemplate.execute(new RedisCallback<String>() {

			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.ping();
			}

		});
	}

	@Override
	public boolean execute() {
		try {
			ping();
		} catch (Throwable e) {
			List<ConnectionFailureHandler> copy = new ArrayList<ConnectionFailureHandler>(listeners);
			listeners.clear();
			for (ConnectionFailureHandler listener : copy) {
				listener.handleException(e);
			}
		}
		return true;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ConnectionFailureHandler) {
			addListener((ConnectionFailureHandler) bean);
		}
		return bean;
	}

	@Override
	public void destroy() throws Exception {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
