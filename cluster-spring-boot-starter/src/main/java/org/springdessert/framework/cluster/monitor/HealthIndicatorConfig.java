package org.springdessert.framework.cluster.monitor;

import org.springdessert.framework.cluster.http.RestClientConfig;
import org.springdessert.framework.cluster.multicast.ApplicationMulticastConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
 * HealthIndicatorConfig
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
@Configuration
public class HealthIndicatorConfig {

	@Bean("applicationCluster")
	@ConditionalOnBean(ApplicationMulticastConfig.class)
	public ApplicationClusterHealthIndicator applicationClusterHealthIndicator() {
		return new ApplicationClusterHealthIndicator();
	}

	@Bean("taskExecutor")
	@ConditionalOnBean(ThreadPoolTaskExecutor.class)
	public TaskExecutorHealthIndicator taskExecutorHealthIndicator() {
		return new TaskExecutorHealthIndicator();
	}

	@Bean("httpStatistic")
	@ConditionalOnBean(RestClientConfig.class)
	public HttpStatisticHealthIndicator httpStatisticHealthIndicator() {
		return new HttpStatisticHealthIndicator();
	}

}
