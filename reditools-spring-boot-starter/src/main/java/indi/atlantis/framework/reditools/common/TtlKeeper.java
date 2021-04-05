package indi.atlantis.framework.reditools.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;

import indi.atlantis.framework.reditools.BeanNames;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * TtlKeeper
 *
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class TtlKeeper {

	@Qualifier(BeanNames.REDIS_TEMPLATE)
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	private ThreadPoolTaskScheduler taskScheduler;

	@PostConstruct
	public void configure() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(8);
		taskScheduler.setThreadFactory(new PooledThreadFactory("ttl-keeper-task-scheduler-"));
		taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		taskScheduler.setAwaitTerminationSeconds(60);
		taskScheduler.initialize();
		this.taskScheduler = taskScheduler;
	}

	public void keepAlive(String key, int timeout) {
		keepAlive(key, timeout, 1);
	}

	public void keepAlive(String key, int timeout, int checkInterval) {
		keepAlive(key, timeout, checkInterval, TimeUnit.SECONDS);
	}

	public void keepAlive(String key, int timeout, int checkInterval, TimeUnit timeUnit) {
		if (timeUnit.compareTo(TimeUnit.SECONDS) < 0) {
			throw new IllegalArgumentException("Don't accept the TimeUnit: " + timeUnit);
		}
		if (redisTemplate.hasKey(key)) {
			redisTemplate.expire(key, timeout, timeUnit);
			futures.put(key, taskScheduler.scheduleAtFixedRate(new KeepAliveTask(key, timeout, timeUnit),
					DateUtils.convertToMillis(checkInterval, timeUnit)));
			if (log.isTraceEnabled()) {
				log.trace("Keeping redis key: {}", key);
			}
		}
	}

	public void cancel(String key) {
		ScheduledFuture<?> future = futures.get(key);
		if (future != null) {
			future.cancel(true);
			if (log.isTraceEnabled()) {
				log.trace("Stop keeping redis key: {}", key);
			}
		}
	}

	@PreDestroy
	public void destroy() {
		if (taskScheduler != null) {
			taskScheduler.shutdown();
		}
	}

	private class KeepAliveTask implements Runnable {

		private final String key;
		private final long timeout;
		private final TimeUnit timeUnit;

		KeepAliveTask(String key, long timeout, TimeUnit timeUnit) {
			this.key = key;
			this.timeout = timeout;
			this.timeUnit = timeUnit;
		}

		@Override
		public void run() {
			try {
				if (redisTemplate.hasKey(key)) {
					redisTemplate.expire(key, timeout, timeUnit);
				} else {
					cancel(key);
				}
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		}

	}

}
